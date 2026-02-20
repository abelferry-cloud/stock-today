package com.me.spring.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * API Key配置属性类
 * 从环境变量读取API密钥配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "api")
public class ApiKeyConfig {

    /**
     * DeepSeek API配置
     */
    private DeepSeekConfig deepseek = new DeepSeekConfig();

    /**
     * DashScope API配置
     */
    private DashScopeConfig dashscope = new DashScopeConfig();

    /**
     * Pinecone API配置
     */
    private PineconeConfig pinecone = new PineconeConfig();

    @Data
    public static class DeepSeekConfig {
        /**
         * DeepSeek API密钥
         */
        private String apiKey;
    }

    @Data
    public static class DashScopeConfig {
        /**
         * 阿里云DashScope API密钥
         */
        private String apiKey;
    }

    @Data
    public static class PineconeConfig {
        /**
         * Pinecone向量数据库API密钥
         */
        private String apiKey;
    }
}
