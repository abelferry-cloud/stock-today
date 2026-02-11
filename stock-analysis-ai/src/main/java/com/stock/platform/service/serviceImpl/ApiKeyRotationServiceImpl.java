package com.stock.platform.service.serviceImpl;

import com.stock.platform.config.ApiKeyRotationManager;
import com.stock.platform.service.ApiKeyRotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * API Key 轮询服务实现
 */
@Service
@RequiredArgsConstructor
public class ApiKeyRotationServiceImpl implements ApiKeyRotationService {

    private final ApiKeyRotationManager rotationManager;

    @Override
    public int getCurrentIndex() {
        return rotationManager.getCurrentIndex();
    }

    @Override
    public int getTotalKeys() {
        return rotationManager.getTotalKeys();
    }

    @Override
    public double getUsageRate() {
        int total = getTotalKeys();
        if (total == 0) {
            return 0.0;
        }
        return (getCurrentIndex() * 100.0) / total;
    }
}
