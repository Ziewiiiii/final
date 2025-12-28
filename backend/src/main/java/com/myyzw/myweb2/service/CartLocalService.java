/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.service;

import com.myyzw.myweb2.domain.entity.CartItemEntity;
import com.myyzw.myweb2.domain.entity.SkuEntity;
import com.myyzw.myweb2.domain.entity.UserEntity;
import com.myyzw.myweb2.domain.repository.CartItemRepository;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 本地购物车
 */
@Service
public class CartLocalService {

    private final CartItemRepository cartItemRepository;
    private final UserLocalService userLocalService;
    private final GoodsLocalService goodsLocalService;

    public CartLocalService(CartItemRepository cartItemRepository,
                            UserLocalService userLocalService,
                            GoodsLocalService goodsLocalService) {
        this.cartItemRepository = cartItemRepository;
        this.userLocalService = userLocalService;
        this.goodsLocalService = goodsLocalService;
    }

    @Transactional
    public Map<String, Object> addItem(String bearerToken, Long skuId, Integer count) {
        UserEntity user = resolveUser(bearerToken);
        Optional<CartItemEntity> existing = cartItemRepository.findByUser(user).stream()
                .filter(ci -> ci.getSkuId().equals(skuId))
                .findFirst();

        CartItemEntity entity = existing.orElseGet(() -> buildCartItem(user, skuId));
        entity.setCount(entity.getCount() == null ? count : entity.getCount() + count);
        cartItemRepository.save(entity);
        return successPayload(List.of(entity));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> list(String bearerToken) {
        UserEntity user = resolveUser(bearerToken);
        List<CartItemEntity> items = cartItemRepository.findByUser(user);
        return successPayload(items);
    }

    @Transactional
    public Map<String, Object> delete(String bearerToken, List<Long> skuIds) {
        UserEntity user = resolveUser(bearerToken);
        cartItemRepository.deleteByUserAndSkuIdIn(user, skuIds);
        return successPayload(Collections.emptyList());
    }

    @Transactional
    public Map<String, Object> merge(String bearerToken, List<Map<String, Object>> data) {
        if (data == null || data.isEmpty()) {
            return successPayload(Collections.emptyList());
        }
        UserEntity user = resolveUser(bearerToken);
        data.forEach(item -> {
            Long skuId = Long.valueOf(item.get("skuId").toString());
            Integer count = Integer.valueOf(item.getOrDefault("count", 1).toString());
            Boolean selected = Boolean.valueOf(item.getOrDefault("selected", true).toString());
            Optional<CartItemEntity> existing = cartItemRepository.findByUser(user).stream()
                    .filter(ci -> ci.getSkuId().equals(skuId))
                    .findFirst();
            CartItemEntity entity = existing.orElseGet(() -> buildCartItem(user, skuId));
            entity.setCount(count);
            entity.setSelected(selected);
            cartItemRepository.save(entity);
        });
        return successPayload(cartItemRepository.findByUser(user));
    }

    private UserEntity resolveUser(String bearerToken) {
        String token = bearerToken == null ? null : bearerToken.replace("Bearer ", "");
        return userLocalService.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("未登录或token无效"));
    }

    private CartItemEntity buildCartItem(UserEntity user, Long skuId) {
        CartItemEntity entity = new CartItemEntity();
        entity.setUser(user);
        entity.setSkuId(skuId);
        SkuEntity sku = goodsLocalService.findSku(skuId)
                .orElseThrow(() -> new IllegalArgumentException("SKU未找到，请先访问商品详情完成本地化"));
        entity.setSpuId(sku.getSpuId());
        entity.setName(sku.getName());
        entity.setPrice(Optional.ofNullable(sku.getPrice()).orElse(BigDecimal.ZERO));
        entity.setPicture(sku.getPicture());
        entity.setAttrsText(sku.getAttrsText());
        entity.setSelected(true);
        entity.setCount(0);
        return entity;
    }

    private Map<String, Object> successPayload(List<CartItemEntity> items) {
        return Map.of(
                "code", 200,
                "message", "success",
                "result", items
        );
    }
}

