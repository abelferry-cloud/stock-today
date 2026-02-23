package com.me.spring.stockanalysisai.pojo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 股票查询DTO
 *
 * @author system
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "股票查询请求参数")
public class StockQueryDTO {

    /**
     * 股票代码
     */
    @Schema(description = "股票代码", example = "000001")
    private String code;

    /**
     * 股票名称
     */
    @Schema(description = "股票名称", example = "平安银行")
    private String name;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期", example = "2024-12-31")
    private LocalDate endDate;

    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "100")
    private Integer pageSize = 100;
}