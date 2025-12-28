/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.repository;

import com.myyzw.myweb2.domain.entity.SkuEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkuRepository extends JpaRepository<SkuEntity, Long> {

    Optional<SkuEntity> findBySkuId(Long skuId);

    List<SkuEntity> findBySpuId(Long spuId);
}

