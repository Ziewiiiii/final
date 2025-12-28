/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.service;

import com.myyzw.myweb2.config.PasswordConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 密码处理服务
 */
@Service
public class PasswordService {
    
    private static final Logger log = LoggerFactory.getLogger(PasswordService.class);
    
    private final PasswordConfig passwordConfig;
    
    public PasswordService(PasswordConfig passwordConfig) {
        this.passwordConfig = passwordConfig;
    }
    
    /**
     * 密码映射表（敏感信息，应该从数据库或加密存储中读取）
     */
    private static final Map<String, String> PASSWORD_MAPPING = new HashMap<>();
    
    static {
        // 这里应该从安全的存储中读取，比如加密的数据库或配置文件
        PASSWORD_MAPPING.put("admin", "admin123");
        PASSWORD_MAPPING.put("user", "user123");
        PASSWORD_MAPPING.put("test", "test123");
    }
    
    /**
     * 验证密码（通过映射表）
     */
    public boolean validatePassword(String account, String inputPassword) {
        String mappedPassword = PASSWORD_MAPPING.get(account);
        if (mappedPassword == null) {
            log.warn("Account not found in password mapping: {}", account);
            return false;
        }
        return mappedPassword.equals(inputPassword);
    }
    
    /**
     * 加密密码
     */
    public String encryptPassword(String password) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                passwordConfig.getEncryptionKey().getBytes(StandardCharsets.UTF_8), 
                passwordConfig.getAlgorithm()
            );
            Cipher cipher = Cipher.getInstance(passwordConfig.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("Password encryption failed", e);
            throw new RuntimeException("Password encryption failed", e);
        }
    }
    
    /**
     * 解密密码
     */
    public String decryptPassword(String encryptedPassword) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                passwordConfig.getEncryptionKey().getBytes(StandardCharsets.UTF_8), 
                passwordConfig.getAlgorithm()
            );
            Cipher cipher = Cipher.getInstance(passwordConfig.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedPassword);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Password decryption failed", e);
            throw new RuntimeException("Password decryption failed", e);
        }
    }
    
    /**
     * 获取映射后的密码
     */
    public String getMappedPassword(String account) {
        return PASSWORD_MAPPING.get(account);
    }
}
