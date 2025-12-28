/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myyzw.myweb2.domain.entity.ApiCacheEntity;
import com.myyzw.myweb2.domain.repository.ApiCacheRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ApiCacheService {

    private static final Logger log = LoggerFactory.getLogger(ApiCacheService.class);

    private final ApiCacheRepository apiCacheRepository;
    private final ObjectMapper objectMapper;

    public ApiCacheService(ApiCacheRepository apiCacheRepository, ObjectMapper objectMapper) {
        this.apiCacheRepository = apiCacheRepository;
        this.objectMapper = objectMapper;
    }

  
    public Mono<Object> getOrFetch(String apiKey, Map<String, Object> params, Supplier<Mono<Object>> fetcher) {
        String paramsHash = hashParams(params);
        return Mono.fromCallable(() -> apiCacheRepository.findByApiKeyAndParamsHash(apiKey, paramsHash))
                .flatMap(optional -> optional
                        .map(cache -> Mono.fromCallable(() -> readPayload(cache.getPayload())))
                        .orElseGet(() -> fetchAndPersist(apiKey, paramsHash, fetcher)));
    }

    private Mono<Object> fetchAndPersist(String apiKey, String paramsHash, Supplier<Mono<Object>> fetcher) {
        return fetcher.get().flatMap(result -> {
            try {
                String payload = objectMapper.writeValueAsString(result);
                ApiCacheEntity entity = apiCacheRepository.findByApiKeyAndParamsHash(apiKey, paramsHash)
                        .orElseGet(() -> new ApiCacheEntity(apiKey, paramsHash, payload));
                entity.touch(payload);
                apiCacheRepository.save(entity);
            } catch (JsonProcessingException e) {
                log.error("序列化接口数据失败: {}", apiKey, e);
            }
            return Mono.just(result);
        });
    }

    private Object readPayload(String payload) {
        try {
            return objectMapper.readValue(payload, Object.class);
        } catch (JsonProcessingException e) {
            log.error("反序列化缓存数据失败，返回空对象", e);
            return Collections.emptyMap();
        }
    }

    private String hashParams(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "NO_PARAMS";
        }
        // 保证有序，避免同一参数不同顺序导致 hash 不一致
        Map<String, Object> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        sorted.forEach((k, v) -> sb.append(k).append('=').append(v).append('&'));
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            log.warn("生成参数哈希失败，退化为原始字符串", e);
            return sb.toString();
        }
    }
}

