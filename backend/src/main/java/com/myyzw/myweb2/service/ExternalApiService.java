/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.service;

import com.myyzw.myweb2.config.ExternalApiConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class ExternalApiService {
    
    private static final Logger log = LoggerFactory.getLogger(ExternalApiService.class);
    
    private final ExternalApiConfig apiConfig;
    private final WebClient webClient;
    
    /**
     * 构造函数，初始化WebClient
     */
    public ExternalApiService(ExternalApiConfig apiConfig) {
        this.apiConfig = apiConfig;
        this.webClient = WebClient.builder()
                .baseUrl(apiConfig.getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
    
    public Mono<Object> get(String path, Map<String, Object> params, String token) {
        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    if (params != null && !params.isEmpty()) {
                        params.forEach(uriBuilder::queryParam);
                    }
                    return uriBuilder.build();
                })
                .headers(headers -> applyAuth(headers, token))
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofMillis(apiConfig.getTimeout()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("External API GET request failed: {}", ex.getMessage());
                    return Mono.error(ex);
                });
    }
    
    public Mono<Object> post(String path, Object body, String token) {
        return webClient.post()
                .uri(path)
                .headers(headers -> applyAuth(headers, token))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofMillis(apiConfig.getTimeout()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("External API POST request failed: {}", ex.getMessage());
                    return Mono.error(ex);
                });
    }
    
    public Mono<Object> delete(String path, Object body, String token) {
        return webClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri(path)
                .headers(headers -> applyAuth(headers, token))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Object.class)
                .timeout(Duration.ofMillis(apiConfig.getTimeout()))
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("External API DELETE request failed: {}", ex.getMessage());
                    return Mono.error(ex);
                });
    }

    private void applyAuth(HttpHeaders headers, String token) {
        if (token == null || token.isBlank()) {
            return;
        }
        String trimmed = token.trim();
        if (trimmed.toLowerCase().startsWith("bearer ")) {
            trimmed = trimmed.substring(7).trim();
        }
        if (!trimmed.isEmpty()) {
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + trimmed);
        }
    }
}
