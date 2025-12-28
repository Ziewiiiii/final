/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.repository;

import com.myyzw.myweb2.domain.entity.ApiCacheEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiCacheRepository extends JpaRepository<ApiCacheEntity, Long> {

    Optional<ApiCacheEntity> findByApiKeyAndParamsHash(String apiKey, String paramsHash);
}

