/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.repository;

import com.myyzw.myweb2.domain.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
}

