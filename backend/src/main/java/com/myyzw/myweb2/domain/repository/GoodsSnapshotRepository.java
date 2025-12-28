/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.repository;

import com.myyzw.myweb2.domain.entity.GoodsSnapshotEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsSnapshotRepository extends JpaRepository<GoodsSnapshotEntity, Long> {

    Optional<GoodsSnapshotEntity> findBySpuId(Long spuId);
}

