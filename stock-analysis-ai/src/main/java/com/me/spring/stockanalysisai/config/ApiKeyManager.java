package com.me.spring.stockanalysisai.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * API Key 管理器
 * 支持多个 API Key 的负载均衡和 429 自动冷却
 */
@Slf4j
@Component
public class ApiKeyManager {

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
     */
    public void init(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("API keys cannot be empty");
        }
        this.apiKeys = keys;
        this.currentIndex.set(0);
        this.keyStatusMap.clear();
        log.info("ApiKeyManager initialized with {} API keys", keys.size());
    }

    /**
     * 获取下一个可用的 API Key（Round Robin）
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
     * 脱敏显示 API Key
     */
    private String maskKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "***" + apiKey.substring(apiKey.length() - 4);
    }
}