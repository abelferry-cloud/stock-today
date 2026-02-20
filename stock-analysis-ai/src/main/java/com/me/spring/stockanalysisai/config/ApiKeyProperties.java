package com.me.spring.stockanalysisai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * DeepSeek API Key 配置属性
 * 支持多个 API Key 的负载均衡和 429 自动冷却
 */
@Component
@ConfigurationProperties(prefix = "spring.ai.deepseek")
@Data
public class ApiKeyProperties {

    private String baseUrl;

    @NestedConfigurationProperty
    private ChatOptions chat = new ChatOptions();

    /**
     * 多个 API Key 列表，用于负载均衡
     */
    private List<String> apiKeys = new ArrayList<>();

    /**
     * 单个 API Key（兼容旧配置）
     */
    private String apiKey;

    @Data
    public static class ChatOptions {
        @NestedConfigurationProperty
        private ChatModelOptions options = new ChatModelOptions();
    }

    @Data
    public static class ChatModelOptions {
        private String model;
        private Double temperature;
    }

    /**
     * 获取所有可用的 API Keys
     * 如果配置了 apiKeys 列表则使用列表，否则使用单个 apiKey
     */
    public List<String> getAllApiKeys() {
        if (apiKeys != null && !apiKeys.isEmpty()) {
            return apiKeys;
        }
        if (apiKey != null && !apiKey.isEmpty()) {
            return List.of(apiKey);
        }
        return new ArrayList<>();
    }

    /**
     * 是否配置了多个 API Key
     */
    public boolean hasMultipleKeys() {
        return apiKeys != null && apiKeys.size() > 1;
    }
}