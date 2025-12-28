/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "password.mapping")
public class PasswordConfig {
    
    /**
     * 加密密钥
     */
    private String encryptionKey = "myweb2-secret-key-2024";
    
    /**
     * 加密算法
     */
    private String algorithm = "AES";
    
    public String getEncryptionKey() {
        return encryptionKey;
    }
    
    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
