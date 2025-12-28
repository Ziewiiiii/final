/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.controller;

import com.myyzw.myweb2.service.CartLocalService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 购物车相关API控制器
 */
@RestController
@RequestMapping("/api/member/cart")
public class CartController extends BaseController {
    
    private final CartLocalService cartLocalService;
    
    public CartController(CartLocalService cartLocalService) {
        this.cartLocalService = cartLocalService;
    }
    
    /**
     * 加入购物车
     */
    @PostMapping
    public Mono<Object> insertCart(@RequestBody(required = false) Map<String, Object> cartData,
                                  @RequestHeader(value = "Authorization", required = false) String token) {
        if (cartData == null || cartData.isEmpty()) {
            return Mono.just(java.util.Map.of("code", 400, "message", "购物车数据不能为空"));
        }
        Long skuId = Long.valueOf(cartData.get("skuId").toString());
        Integer count = Integer.valueOf(cartData.getOrDefault("count", 1).toString());
        return Mono.fromCallable(() -> cartLocalService.addItem(token, skuId, count));
    }
    
    /**
     * 获取购物车列表
     */
    @GetMapping
    public Mono<Object> getCartList(@RequestHeader(value = "Authorization", required = false) String token) {
        return Mono.fromCallable(() -> cartLocalService.list(token));
    }
    
    /**
     * 删除购物车商品
     */
    @DeleteMapping
    public Mono<Object> deleteCart(@RequestBody(required = false) Map<String, Object> deleteData,
                                  @RequestHeader(value = "Authorization", required = false) String token) {
        if (deleteData == null || deleteData.isEmpty()) {
            return Mono.just(java.util.Map.of("code", 400, "message", "删除数据不能为空"));
        }
        List<Long> ids = ((List<?>) deleteData.get("ids")).stream().map(o -> Long.valueOf(o.toString())).toList();
        return Mono.fromCallable(() -> cartLocalService.delete(token, ids));
    }
    
    /**
     * 合并购物车
     */
    @PostMapping("/merge")
    public Mono<Object> mergeCart(@RequestBody(required = false) Object mergeData,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        if (mergeData == null || 
            (mergeData instanceof java.util.List && ((java.util.List<?>) mergeData).isEmpty())) {
            return Mono.just(java.util.Map.of("code", 200, "message", "success", "result", java.util.Collections.emptyList()));
        }
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> body = (List<Map<String, Object>>) mergeData;
        return Mono.fromCallable(() -> cartLocalService.merge(token, body));
    }
}
