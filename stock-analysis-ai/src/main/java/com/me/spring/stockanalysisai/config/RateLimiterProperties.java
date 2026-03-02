package com.me.spring.stockanalysisai.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 限流器配置类
 * 为每个 API Key 提供独立的 RateLimiter
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "stock.ratelimiter")
public class RateLimiterProperties {

    /**
     * 是否启用限流
     */
    private boolean enabled = true;

    /**
     * 令牌桶容量（允许的最大突发请求数）
     */
    private int limitForPeriod = 5;

    /**
     * 令牌刷新周期（每秒补充多少次令牌）
     */
    private Duration limitRefreshPeriod = Duration.ofSeconds(1);

    /**
     * 获取令牌的超时时间
     */
    private Duration timeoutDuration = Duration.ofSeconds(3);

    /**
     * 创建 RateLimiterRegistry Bean
     */
    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        io.github.resilience4j.ratelimiter.RateLimiterConfig config = io.github.resilience4j.ratelimiter.RateLimiterConfig.custom()
                .limitForPeriod(limitForPeriod)
                .limitRefreshPeriod(limitRefreshPeriod)
                .timeoutDuration(timeoutDuration)
                .build();

        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        log.info("RateLimiter initialized: limitForPeriod={}, limitRefreshPeriod={}, timeoutDuration={}",
                limitForPeriod, limitRefreshPeriod, timeoutDuration);
        return registry;
    }

    /**
     * 为指定 API Key 创建或获取 RateLimiter
     */
    public RateLimiter getRateLimiter(RateLimiterRegistry registry, String apiKey) {
        String limiterName = "api-key-" + hashApiKey(apiKey);
        return registry.rateLimiter(limiterName);
    }

    /**
     * 为 API Key 生成唯一的限流器名称
     * 使用 hash 避免 Key 过长
     */
    private String hashApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "default";
        }
        // 取 API Key 的前 8 位和后 4 位作为标识
        int end = Math.min(apiKey.length(), 8);
        String prefix = apiKey.substring(0, end);
        String suffix = apiKey.length() > 4 ? apiKey.substring(apiKey.length() - 4) : "";
        return prefix + suffix;
    }
}
