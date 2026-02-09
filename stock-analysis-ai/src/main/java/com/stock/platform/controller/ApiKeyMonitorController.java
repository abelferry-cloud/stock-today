package com.stock.platform.controller;

import com.stock.platform.common.result.Result;
import com.stock.platform.dto.response.ApiKeyUsageResponse;
import com.stock.platform.service.ApiKeyRotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API Key 监控控制器
 */
@Schema(description = "API Key监控接口")
@Tag(name = "API Key监控", description = "监控API Key的使用情况，包括总数量、当前索引、使用率等")
@Slf4j
@RestController
@RequestMapping("/monitor")
@RequiredArgsConstructor
public class ApiKeyMonitorController {

    private final ApiKeyRotationService rotationService;

    /**
     * 获取 API Key 使用情况
     * GET /api/monitor/api-key-usage
     */
    @Operation(
            summary = "获取API Key使用情况",
            description = "查询当前API Key池的使用统计信息，包括总Key数量、当前使用的Key索引和使用率",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "成功返回API Key使用情况",
                            content = @Content(schema = @Schema(implementation = ApiKeyUsageResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "服务器内部错误")
            }
    )
    @GetMapping("/api-key-usage")
    public Result<ApiKeyUsageResponse> getApiKeyUsage() {
        log.debug("查询 API Key 使用情况");

        ApiKeyUsageResponse response = ApiKeyUsageResponse.builder()
                .totalKeys(rotationService.getTotalKeys())
                .currentIndex(rotationService.getCurrentIndex())
                .usageRate(rotationService.getUsageRate())
                .build();

        return Result.success(response);
    }
}
