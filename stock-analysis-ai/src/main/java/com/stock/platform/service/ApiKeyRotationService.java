package com.stock.platform.service;

/**
 * API Key 轮询服务接口
 */
public interface ApiKeyRotationService {

    /**
     * 获取当前 API Key 索引
     */
    int getCurrentIndex();

    /**
     * 获取 API Key 总数
     */
    int getTotalKeys();

    /**
     * 获取使用率（百分比）
     */
    double getUsageRate();
}
