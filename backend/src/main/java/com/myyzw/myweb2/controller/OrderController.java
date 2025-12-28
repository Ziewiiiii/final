/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.controller;

import com.myyzw.myweb2.service.OrderLocalService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 订单相关API控制器
 */
@RestController
@RequestMapping("/api/member/order")
public class OrderController extends BaseController {
    
    private final OrderLocalService orderLocalService;
    
    public OrderController(OrderLocalService orderLocalService) {
        this.orderLocalService = orderLocalService;
    }
    
    /**
     * 获取用户订单列表
     */
    @GetMapping
    public Mono<Object> getUserOrder(@RequestParam(required = false) Integer orderState,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "2") int pageSize,
                                    @RequestHeader(value = "Authorization", required = false) String token) {
        return Mono.fromCallable(() -> orderLocalService.list(token, orderState, page, pageSize));
    }
    
    /**
     * 获取结算信息
     */
    @GetMapping("/pre")
    public Mono<Object> getCheckoutInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        return Mono.fromCallable(() -> orderLocalService.checkout(token));
    }
    
    /**
     * 创建订单
     */
    @PostMapping
    public Mono<Object> createOrder(@RequestBody java.util.Map<String, Object> orderData,
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        return Mono.fromCallable(() -> orderLocalService.create(token, orderData));
    }
    
    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    public Mono<Object> getOrderDetail(@PathVariable String id,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        return Mono.fromCallable(() -> orderLocalService.detail(token, Long.valueOf(id)));
    }
}
