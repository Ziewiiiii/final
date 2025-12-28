/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "external.api")
public class ExternalApiConfig {
    
    private String baseUrl = "http://pcapi-xiaotuxian-front-devtest.itheima.net";
    
    private int timeout = 20000;
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
