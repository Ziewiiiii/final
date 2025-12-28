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
 * 分类相关API控制器
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController extends BaseController {
    
    private final ExternalApiService externalApiService;
    private final ApiCacheService apiCacheService;
    
    public CategoryController(ExternalApiService externalApiService, ApiCacheService apiCacheService) {
        this.externalApiService = externalApiService;
        this.apiCacheService = apiCacheService;
    }
    
    /**
     * 获取分类数据
     */
    @GetMapping
    public Mono<Object> getTopCategory(@RequestParam String id,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> params = Map.of("id", id);
        return apiCacheService.getOrFetch(
                "category-top",
                params,
                () -> externalApiService.get("/category", params, null)
        );
    }
    
    /**
     * 获取二级分类筛选条件
     */
    @GetMapping("/sub/filter")
    public Mono<Object> getCategoryFilter(@RequestParam String id,
                                         @RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> params = Map.of("id", id);
        return apiCacheService.getOrFetch(
                "category-sub-filter",
                params,
                () -> externalApiService.get("/category/sub/filter", params, null)
        );
    }
    
    /**
     * 获取子分类商品
     */
    @PostMapping("/goods/temporary")
    public Mono<Object> getSubCategory(@RequestBody Map<String, Object> data,
                                      @RequestHeader(value = "Authorization", required = false) String token) {
        return apiCacheService.getOrFetch(
                "category-goods-temporary",
                data,
                () -> externalApiService.post("/category/goods/temporary", data, null)
        );
    }
}
