/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.service;

import com.myyzw.myweb2.domain.entity.CartItemEntity;
import com.myyzw.myweb2.domain.entity.OrderEntity;
import com.myyzw.myweb2.domain.entity.OrderItemEntity;
import com.myyzw.myweb2.domain.entity.UserEntity;
import com.myyzw.myweb2.domain.repository.CartItemRepository;
import com.myyzw.myweb2.domain.repository.OrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 订单本地化服务，基于购物车数据生成订单。
 */
@Service
public class OrderLocalService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserLocalService userLocalService;
    private final GoodsLocalService goodsLocalService;
    private final EmailService emailService;

    public OrderLocalService(OrderRepository orderRepository,
                             CartItemRepository cartItemRepository,
                             UserLocalService userLocalService,
                             GoodsLocalService goodsLocalService,
                             EmailService emailService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userLocalService = userLocalService;
        this.goodsLocalService = goodsLocalService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> list(String bearerToken, Integer orderState, int page, int pageSize) {
        UserEntity user = resolveUser(bearerToken);
        PageRequest pageable = PageRequest.of(Math.max(page - 1, 0), pageSize);
        Page<OrderEntity> orderPage = (orderState == null)
                ? orderRepository.findByUser(user, pageable)
                : orderRepository.findByUserAndOrderState(user, orderState, pageable);
        List<Map<String, Object>> items = orderPage.stream().map(this::toMap).collect(Collectors.toList());
        return Map.of(
                "code", 200,
                "message", "success",
                "result", Map.of(
                        "items", items,
                        "counts", orderPage.getTotalElements()
                )
        );
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Map<String, Object> create(String bearerToken, Map<String, Object> orderData) {
        UserEntity user = resolveUser(bearerToken);
        List<Map<String, Object>> goods = (List<Map<String, Object>>) orderData.getOrDefault("goods", Collections.emptyList());
        if (goods.isEmpty()) {
            throw new IllegalArgumentException("订单中没有商品");
        }
        OrderEntity order = new OrderEntity();
        order.setUser(user);
        order.setOrderState(1);
        order.setCreateTime(LocalDateTime.now());
        order.setCountdown(30L * 60L); // 30 分钟支付窗口

        BigDecimal total = BigDecimal.ZERO;
        List<Long> invalidSkuIds = new ArrayList<>();
        for (Map<String, Object> g : goods) {
            Long skuId = Long.valueOf(g.get("skuId").toString());
            Integer count = Integer.valueOf(g.getOrDefault("count", 1).toString());
            CartItemEntity cartItem = cartItemRepository.findByUser(user).stream()
                    .filter(ci -> ci.getSkuId().equals(skuId))
                    .findFirst()
                    .orElse(null);

            OrderItemEntity oi = new OrderItemEntity();
            if (cartItem != null) {
                oi.setSkuId(cartItem.getSkuId());
                oi.setSpuId(cartItem.getSpuId());
                oi.setName(cartItem.getName());
                oi.setAttrsText(cartItem.getAttrsText());
                oi.setQuantity(count);
                oi.setRealPay(cartItem.getPrice().multiply(BigDecimal.valueOf(count)));
                oi.setImage(cartItem.getPicture());
            } else {
                var skuOpt = goodsLocalService.findSku(skuId);
                if (skuOpt.isEmpty()) {
                    invalidSkuIds.add(skuId);
                    continue;
                }
                var sku = skuOpt.get();
                oi.setSkuId(sku.getSkuId());
                oi.setSpuId(sku.getSpuId());
                oi.setName(sku.getName());
                oi.setAttrsText(sku.getAttrsText());
                oi.setQuantity(count);
                BigDecimal price = sku.getPrice() != null ? sku.getPrice() : BigDecimal.ZERO;
                oi.setRealPay(price.multiply(BigDecimal.valueOf(count)));
                oi.setImage(sku.getPicture());
            }
            order.addItem(oi);
            total = total.add(oi.getRealPay());
        }

        // 清理无效的购物车行（不存在的 SKU 或未能补全的 SKU）
        if (!invalidSkuIds.isEmpty()) {
            cartItemRepository.deleteByUserAndSkuIdIn(user, invalidSkuIds);
        }
        // 同时清理 skuId 为空的脏数据
        List<CartItemEntity> nullSkuItems = cartItemRepository.findByUser(user).stream()
                .filter(ci -> ci.getSkuId() == null)
                .toList();
        if (!nullSkuItems.isEmpty()) {
            cartItemRepository.deleteAll(nullSkuItems);
        }

        if (order.getItems().isEmpty()) {
            throw new IllegalArgumentException("购物车存在失效商品，已清理，请重新选择商品下单");
        }

        order.setPayMoney(total);
        order.setPostFee(BigDecimal.ZERO);
        OrderEntity saved = orderRepository.save(order);
        // 下单后清理购物车中对应的SKU
        List<Long> skuIds = goods.stream().map(g -> Long.valueOf(g.get("skuId").toString())).toList();
        cartItemRepository.deleteByUserAndSkuIdIn(user, skuIds);

        // 发送订单确认邮件
        String email = (String) orderData.getOrDefault("email", null);
        if (email != null && !email.trim().isEmpty()) {
            try {
                emailService.sendOrderConfirmationEmail(email, saved.getId(), total.toString());
            } catch (Exception e) {
                // 邮件发送失败不影响订单创建
                System.err.println("发送订单确认邮件失败: " + e.getMessage());
            }
        }

        return Map.of(
                "code", 200,
                "message", "success",
                "result", Map.of("id", saved.getId())
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> detail(String bearerToken, Long id) {
        UserEntity user = resolveUser(bearerToken);
        OrderEntity order = orderRepository.findById(Objects.requireNonNull(id, "订单ID不可为空"))
                .filter(o -> o.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("订单不存在"));
        return Map.of(
                "code", 200,
                "message", "success",
                "result", toMap(order)
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> checkout(String bearerToken) {
        UserEntity user = resolveUser(bearerToken);
        List<CartItemEntity> carts = cartItemRepository.findByUser(user).stream()
                .filter(CartItemEntity::getSelected)
                .collect(Collectors.toList());
        BigDecimal totalPrice = carts.stream()
                .map(ci -> ci.getPrice().multiply(BigDecimal.valueOf(ci.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> result = new HashMap<>();
        result.put("goods", carts);
        result.put("summary", Map.of(
                "totalPrice", totalPrice,
                "postFee", BigDecimal.ZERO,
                "payPrice", totalPrice
        ));
        result.put("userAddresses", demoAddresses());
        return Map.of(
                "code", 200,
                "message", "success",
                "result", result
        );
    }

    private List<Map<String, Object>> demoAddresses() {
        return List.of(
                Map.of("id", 1, "receiver", "张三", "contact", "13800000001", "address", "北京市朝阳区芍药居", "isDefault", 0),
                Map.of("id", 2, "receiver", "李四", "contact", "13800000002", "address", "上海市浦东新区世纪大道", "isDefault", 1)
        );
    }

    private Map<String, Object> toMap(OrderEntity order) {
        return Map.of(
                "id", order.getId(),
                "orderState", order.getOrderState(),
                "payMoney", order.getPayMoney(),
                "postFee", order.getPostFee(),
                "createTime", order.getCreateTime(),
                "countdown", order.getCountdown(),
                "skus", order.getItems().stream().map(oi -> Map.of(
                        "id", oi.getId(),
                        "name", oi.getName(),
                        "image", oi.getImage(),
                        "attrsText", oi.getAttrsText(),
                        "quantity", oi.getQuantity(),
                        "realPay", oi.getRealPay()
                )).collect(Collectors.toList())
        );
    }

    private UserEntity resolveUser(String bearerToken) {
        String token = bearerToken == null ? null : bearerToken.replace("Bearer ", "");
        return userLocalService.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("未登录或token无效"));
    }
}

