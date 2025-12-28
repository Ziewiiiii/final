/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 商品详情
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "goods_snapshot")
public class GoodsSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long spuId;

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String payload;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public GoodsSnapshotEntity(Long spuId, String payload) {
        this.spuId = spuId;
        this.payload = payload;
    }
}

