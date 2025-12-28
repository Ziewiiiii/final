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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SKU 基础信息。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "goods_sku", indexes = @Index(name = "idx_sku_spu", columnList = "spuId"))
public class SkuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long skuId;

    @Column(nullable = false)
    private Long spuId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    private BigDecimal oldPrice;

    @Column(nullable = false)
    private Integer inventory;

    @Column(length = 512)
    private String attrsText;

    @Column(length = 512)
    private String picture;
}

