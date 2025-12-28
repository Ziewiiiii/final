/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.controller;

import com.myyzw.myweb2.service.ExternalApiService;
import com.myyzw.myweb2.service.PasswordService;
import com.myyzw.myweb2.service.UserLocalService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 用户相关API控制器
 */
@RestController
@RequestMapping("/api")
public class UserController extends BaseController {
    
    private final ExternalApiService externalApiService;
    private final PasswordService passwordService;
    private final UserLocalService userLocalService;
    
    public UserController(ExternalApiService externalApiService,
                          PasswordService passwordService,
                          UserLocalService userLocalService) {
        this.externalApiService = externalApiService;
        this.passwordService = passwordService;
        this.userLocalService = userLocalService;
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Mono<Object> login(@RequestBody Map<String, String> loginData) {
        String account = loginData.get("account");
        String password = loginData.get("password");
        
        // 优先使用本地账户体系
        return Mono.fromCallable(() -> {
            try {
                Map<String, Object> result = userLocalService.login(account, password);
                return Map.of("code", 200, "message", "success", "result", result);
            } catch (IllegalArgumentException ex) {
               
                return externalApiService.post("/login", loginData, null).block();
            }
        });
    }
    
    /**
     * 获取喜欢列表
     */
    @GetMapping("/goods/relevant")
    public Mono<Object> getLikeList(@RequestParam(defaultValue = "4") int limit,
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        return externalApiService.get("/goods/relevant", 
            Map.of("limit", limit), null);
    }
}
