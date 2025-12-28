/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.repository;

import com.myyzw.myweb2.domain.entity.OrderEntity;
import com.myyzw.myweb2.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findByUser(UserEntity user, Pageable pageable);

    Page<OrderEntity> findByUserAndOrderState(UserEntity user, Integer orderState, Pageable pageable);
}

