/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.controller;

import com.myyzw.myweb2.service.ApiCacheService;
import com.myyzw.myweb2.service.ExternalApiService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 首页相关API控制器
 */
@RestController
@RequestMapping("/api/home")
public class HomeController extends BaseController {
    
    private final ExternalApiService externalApiService;
    private final ApiCacheService apiCacheService;
    
    public HomeController(ExternalApiService externalApiService, ApiCacheService apiCacheService) {
        this.externalApiService = externalApiService;
        this.apiCacheService = apiCacheService;
    }
    
    /**
     * 获取Banner
     */
    @GetMapping("/banner")
    public Mono<Object> getBanner(@RequestParam(defaultValue = "1") String distributionSite,
                                  @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> params = Map.of("distributionSite", distributionSite);
        return apiCacheService.getOrFetch(
                "home-banner",
                params,
                () -> externalApiService.get("/home/banner", params, null)
        );
    }
    
    /**
     * 获取新鲜好物
     */
    @GetMapping("/new")
    public Mono<Object> getNewGoods(@RequestHeader(value = "Authorization", required = false) String token) {
        return apiCacheService.getOrFetch(
                "home-new",
                Map.of(),
                () -> externalApiService.get("/home/new", null, null)
        );
    }
    
    /**
     * 获取人气推荐
     */
    @GetMapping("/hot")
    public Mono<Object> getHotGoods(@RequestHeader(value = "Authorization", required = false) String token) {
        return apiCacheService.getOrFetch(
                "home-hot",
                Map.of(),
                () -> externalApiService.get("/home/hot", null, null)
        );
    }
    
    /**
     * 获取所有商品模块
     */
    @GetMapping("/goods")
    public Mono<Object> getGoods(@RequestHeader(value = "Authorization", required = false) String token) {
        return apiCacheService.getOrFetch(
                "home-goods",
                Map.of(),
                () -> externalApiService.get("/home/goods", null, null)
        );
    }
    
    /**
     * 获取头部分类
     */
    @GetMapping("/category/head")
    public Mono<Object> getCategoryHead(@RequestHeader(value = "Authorization", required = false) String token) {
        return apiCacheService.getOrFetch(
                "home-category-head",
                Map.of(),
                () -> externalApiService.get("/home/category/head", null, null)
        );
    }
}
