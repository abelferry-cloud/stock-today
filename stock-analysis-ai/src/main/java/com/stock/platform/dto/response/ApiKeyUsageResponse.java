package com.stock.platform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API Key 使用情况响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyUsageResponse {

    /**
     * API Key 总数
     */
    private Integer totalKeys;

    /**
     * 当前索引
     */
    private Integer currentIndex;

    /**
     * 使用率（百分比）
     */
    private Double usageRate;
}
