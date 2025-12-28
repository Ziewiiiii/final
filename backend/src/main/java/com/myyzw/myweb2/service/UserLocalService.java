/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.service;

import com.myyzw.myweb2.domain.entity.UserEntity;
import com.myyzw.myweb2.domain.repository.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 本地用户登录与Token管理。
 */
@Service
public class UserLocalService {

    private final UserRepository userRepository;

    public UserLocalService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Map<String, Object> login(String account, String password) {
        Optional<UserEntity> optional = userRepository.findByAccount(account);
        UserEntity user = optional.orElseGet(() -> bootstrapUser(account, password));
        // 简单口令校验，可替换为加密校验
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("账号或密码错误");
        }
        // 生成新的 token
        String token = generateToken(account);
        user.setToken(token);
        userRepository.save(user);
        return Map.of(
                "id", user.getId(),
                "account", user.getAccount(),
                "nickname", Optional.ofNullable(user.getNickname()).orElse(user.getAccount()),
                "avatar", Optional.ofNullable(user.getAvatar()).orElse(""),
                "mobile", Optional.ofNullable(user.getMobile()).orElse(""),
                "token", token
        );
    }

    public Optional<UserEntity> findByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        
        try {
            String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
            String account = decoded.split(":")[0];
            return userRepository.findByAccount(account);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private UserEntity bootstrapUser(String account, String password) {
        UserEntity entity = new UserEntity();
        entity.setAccount(account);
        entity.setPassword(password);
        entity.setNickname(account);
        entity.setAvatar("https://dummyimage.com/200x200/91c1ff/ffffff.png&text=" + account);
        return userRepository.save(entity);
    }

    private String generateToken(String account) {
        String raw = account + ":" + Instant.now().toEpochMilli();
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}

