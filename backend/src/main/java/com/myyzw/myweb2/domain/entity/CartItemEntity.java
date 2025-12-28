/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 购物车行。
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cart_item")
public class CartItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Long skuId;

    @Column(nullable = false)
    private Long spuId;

    @Column(nullable = false)
    private String name;

    @Column(length = 512)
    private String attrsText;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private Boolean selected = Boolean.TRUE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 512)
    private String picture;
}

