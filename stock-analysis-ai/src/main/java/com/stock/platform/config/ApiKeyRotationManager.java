package com.stock.platform.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * API Key 轮询管理器
 * 实现 Key1 → Key2 → Key3 → Key4 → Key1... 循环
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "app.api-key")
@Data
public class ApiKeyRotationManager {

    /**
     * API Key 列表
     */
    private List<String> keys;

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * 初始化方法，验证配置
     */
    @PostConstruct
    public void init() {
        log.info("API Key 轮询管理器初始化完成，共 {} 个 Key", keys != null ? keys.size() : 0);
    }

    /**
     * 获取当前轮询的 API Key
     * 线程安全：Key1 → Key2 → Key3 → Key4 → Key1...
     */
    public String getCurrentApiKey() {
        if (keys == null || keys.isEmpty()) {
            log.warn("API Key 列表为空");
            return null;
        }

        // 原子操作：获取当前索引并递增（取模循环）
        int index = currentIndex.getAndUpdate(
                old -> (old + 1) % keys.size()
        );

        String selectedKey = keys.get(index);
        log.debug("轮询 API Key[{}]: {}...", index, maskApiKey(selectedKey));

        return selectedKey;
    }

    /**
     * 获取当前索引（用于监控）
     */
    public int getCurrentIndex() {
        return currentIndex.get();
    }

    /**
     * 获取 API Key 总数
     */
    public int getTotalKeys() {
        return (keys == null || keys.isEmpty()) ? 0 : keys.size();
    }

    /**
     * 掩码 API Key（日志脱敏）
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 8) {
            return "***";
        }
        return apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4);
    }
}
