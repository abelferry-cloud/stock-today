package com.me.spring.stockanalysisai.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * API Key 管理器
 * 支持多个 API Key 的负载均衡和 429 自动冷却
 * 集成 Resilience4j RateLimiter 进行主动限流
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyManager {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final RateLimiterProperties rateLimiterProperties;

    /**
     * 初始冷却时间（毫秒）
     */
    private static final long INITIAL_COOLING_TIME = 60000; // 1分钟

    /**
     * 最大冷却时间（毫秒）
     */
    private static final long MAX_COOLING_TIME = 600000; // 10分钟

    /**
     * 最大退避次数
     */
    private static final int MAX_BACKOFF_COUNT = 5;

    /**
     * API Key 列表
     */
    private volatile List<String> apiKeys;

    /**
     * 当前索引（Round Robin）
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * Key 状态映射
     */
    private final ConcurrentHashMap<String, KeyStatus> keyStatusMap = new ConcurrentHashMap<>();

    /**
     * 每个 API Key 独立的 RateLimiter
     */
    private final ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    /**
     * API Key 状态，包含冷却信息和退避次数
     */
    @Data
    private static class KeyStatus {
        private int backoffCount = 0;
        private long coolingUntil = 0;

        public void incrementBackoff() {
            this.backoffCount++;
        }

        public void reset() {
            this.backoffCount = 0;
            this.coolingUntil = 0;
        }
    }

    /**
     * 初始化 API Keys
     * 为每个 Key 创建独立的 RateLimiter
     */
    public void init(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("API keys cannot be empty");
        }
        this.apiKeys = keys;
        this.currentIndex.set(0);
        this.keyStatusMap.clear();
        this.rateLimiterMap.clear();

        // 为每个 API Key 创建独立的 RateLimiter
        for (String key : keys) {
            RateLimiter rateLimiter = rateLimiterProperties.getRateLimiter(rateLimiterRegistry, key);
            rateLimiterMap.put(key, rateLimiter);
            log.debug("Created RateLimiter for API key: {}", maskKey(key));
        }

        log.info("ApiKeyManager initialized with {} API keys", keys.size());
    }

    /**
     * 获取下一个可用的 API Key（Round Robin）
     * 同时获取限流许可，如果限流则抛出异常
     */
    public synchronized String getNextKey() {
        if (apiKeys == null || apiKeys.isEmpty()) {
            throw new IllegalStateException("API keys not initialized");
        }

        int size = apiKeys.size();
        // 尝试找到非冷却中的 Key
        for (int i = 0; i < size; i++) {
            int index = currentIndex.getAndIncrement() % size;
            String key = apiKeys.get(index);
            if (!isCooling(key)) {
                log.debug("Using API key at index {}", index);
                return key;
            }
        }

        // 所有 Key 都在冷却中，返回最先过期的 Key
        long earliestExpiry = Long.MAX_VALUE;
        String earliestKey = apiKeys.get(0);
        for (String key : apiKeys) {
            KeyStatus status = keyStatusMap.get(key);
            if (status != null && status.getCoolingUntil() < earliestExpiry) {
                earliestExpiry = status.getCoolingUntil();
                earliestKey = key;
            }
        }
        log.warn("All API keys are cooling, returning earliest expiring key");
        return earliestKey;
    }

    /**
     * 获取 API Key 并申请限流许可
     * @return 可用的 API Key
     * @throws io.github.resilience4j.ratelimiter.RequestNotPermitted 如果限流
     */
    public String acquirePermissionAndGetKey() {
        String key = getNextKey();
        acquirePermission(key);
        return key;
    }

    /**
     * 为指定 API Key 获取限流许可
     * 在请求发送前调用，主动预防 429 错误
     *
     * @param apiKey API Key
     * @throws io.github.resilience4j.ratelimiter.RequestNotPermitted 如果限流
     */
    public void acquirePermission(String apiKey) {
        if (!rateLimiterProperties.isEnabled()) {
            log.debug("Rate limiter is disabled, skipping permission check");
            return;
        }

        RateLimiter rateLimiter = rateLimiterMap.get(apiKey);
        if (rateLimiter == null) {
            log.warn("RateLimiter not found for API key: {}, skipping permission check", maskKey(apiKey));
            return;
        }

        // 尝试获取令牌，如果获取失败会抛出 RequestNotPermitted 异常
        if (!rateLimiter.acquirePermission(1)) {
            log.warn("Rate limit exceeded for API key: {}", maskKey(apiKey));
        }
    }

    /**
     * 检查 Key 是否处于冷却状态
     */
    private boolean isCooling(String apiKey) {
        KeyStatus status = keyStatusMap.get(apiKey);
        if (status == null) {
            return false;
        }
        return System.currentTimeMillis() < status.getCoolingUntil();
    }

    /**
     * 标记 Key 触发 429，进入指数退避冷却
     */
    public void markCooling(String apiKey) {
        KeyStatus status = keyStatusMap.computeIfAbsent(apiKey, k -> new KeyStatus());
        status.incrementBackoff();

        // 检查是否超过最大退避次数，超过则重置
        if (status.getBackoffCount() > MAX_BACKOFF_COUNT) {
            log.warn("API key {} exceeded max backoff count, resetting", maskKey(apiKey));
            status.reset();
            return;
        }

        long coolingTime = calculateCoolingTime(status.getBackoffCount());
        status.setCoolingUntil(System.currentTimeMillis() + coolingTime);

        log.warn("API key {} triggered 429, entering cooling for {} ms (backoff count: {})",
                maskKey(apiKey), coolingTime, status.getBackoffCount());
    }

    /**
     * 计算冷却时间（指数退避）
     */
    private long calculateCoolingTime(int backoffCount) {
        long time = INITIAL_COOLING_TIME * (1L << (backoffCount - 1)); // 1min, 2min, 4min, 8min...
        return Math.min(time, MAX_COOLING_TIME);
    }

    /**
     * 重置 Key 的冷却状态
     */
    public void resetKey(String apiKey) {
        KeyStatus status = keyStatusMap.get(apiKey);
        if (status != null) {
            status.reset();
            log.info("API key {} cooling status reset", maskKey(apiKey));
        }
    }

    /**
     * 获取当前 API Keys 列表
     */
    public List<String> getApiKeys() {
        return apiKeys;
    }

    /**
     * 获取 Key 状态信息（用于监控）
     */
    public String getKeyStatusInfo() {
        StringBuilder sb = new StringBuilder();
        if (apiKeys != null) {
            for (String key : apiKeys) {
                KeyStatus status = keyStatusMap.get(key);
                if (status != null) {
                    long remaining = Math.max(0, status.getCoolingUntil() - System.currentTimeMillis());
                    sb.append(maskKey(key))
                            .append(": backoff=")
                            .append(status.getBackoffCount())
                            .append(", remaining=")
                            .append(remaining / 1000)
                            .append("s; ");
                } else {
                    sb.append(maskKey(key)).append(": OK; ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取限流器状态信息（用于监控）
     */
    public String getRateLimiterStatusInfo() {
        StringBuilder sb = new StringBuilder();
        if (apiKeys != null) {
            for (String key : apiKeys) {
                RateLimiter rateLimiter = rateLimiterMap.get(key);
                if (rateLimiter != null) {
                    sb.append(maskKey(key))
                            .append(": OK; ");
                } else {
                    sb.append(maskKey(key)).append(": N/A; ");
                }
            }
        }
        return sb.toString();
    }

    /**
     * 脱敏显示 API Key
     */
    public String maskKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "***" + apiKey.substring(apiKey.length() - 4);
    }
}