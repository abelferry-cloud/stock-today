package com.stock.platform.service;

import com.stock.platform.config.ApiKeyRotationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * API Key 轮询服务
 */
@Service
@RequiredArgsConstructor
public class ApiKeyRotationService {

    private final ApiKeyRotationManager rotationManager;

    /**
     * 获取当前 API Key 索引
     */
    public int getCurrentIndex() {
        return rotationManager.getCurrentIndex();
    }

    /**
     * 获取 API Key 总数
     */
    public int getTotalKeys() {
        return rotationManager.getTotalKeys();
    }

    /**
     * 获取使用率（百分比）
     */
    public double getUsageRate() {
        int total = getTotalKeys();
        if (total == 0) {
            return 0.0;
        }
        return (getCurrentIndex() * 100.0) / total;
    }
}
