/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.domain.repository;

import com.myyzw.myweb2.domain.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByAccount(String account);
}

