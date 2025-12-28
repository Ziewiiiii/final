/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "api_cache",
        indexes = {
                @Index(name = "idx_api_cache_key", columnList = "apiKey"),
                @Index(name = "idx_api_cache_params", columnList = "paramsHash")
        },
        uniqueConstraints = @UniqueConstraint(name = "uk_api_cache_key_params", columnNames = {"apiKey", "paramsHash"})
)
public class ApiCacheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 对应的接口标识，如 home-banner / goods-detail 等
     */
    @Column(nullable = false, length = 64)
    private String apiKey;

    /**
     * 参数哈希，防止同一接口不同参数冲突
     */
    @Column(nullable = false, length = 128)
    private String paramsHash;

    /**
     * 原始的 JSON 载荷
     */
    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ApiCacheEntity(String apiKey, String paramsHash, String payload) {
        this.apiKey = apiKey;
        this.paramsHash = paramsHash;
        this.payload = payload;
    }

    public void touch(String payload) {
        this.payload = payload;
        this.updatedAt = LocalDateTime.now();
    }
}

