/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.controller;

import com.myyzw.myweb2.service.GoodsLocalService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * 商品相关API控制器
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController extends BaseController {
    
    private final GoodsLocalService goodsLocalService;
    
    public GoodsController(GoodsLocalService goodsLocalService) {
        this.goodsLocalService = goodsLocalService;
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping
    public Mono<Object> getDetail(@RequestParam String id,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        return goodsLocalService.getGoodsDetail(Long.valueOf(id), null);
    }
    
    /**
     * 获取热榜商品
     */
    @GetMapping("/hot")
    public Mono<Object> getHotGoods(@RequestParam String id,
                                   @RequestParam int type,
                                   @RequestParam(defaultValue = "3") int limit,
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        return goodsLocalService.getHotGoods(Long.valueOf(id), type, limit, null);
    }
}
