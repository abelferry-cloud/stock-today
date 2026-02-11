package com.me.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 定义每周股票信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "每周股票信息")
public class StockWeeklyInfoDomain {

    /**
     * 平均价格
     */
    @Schema(description = "平均价格")
    private BigDecimal avgPrice;

    /**
     * 最低价
     */
    @Schema(description = "最低价")
    private BigDecimal minPrice;

    /**
     * 开盘价
     */
    @Schema(description = "开盘价")
    private BigDecimal openPrice;

    /**
     * 最高价
     */
    @Schema(description = "最高价")
    private BigDecimal maxPrice;

    /**
     * 收盘价
     */
    @Schema(description = "收盘价")
    private BigDecimal closePrice;

    /**
     * 最高时间
     */
    @Schema(description = "最高时间")
    private Date maxTime;

    /**
     * 股票编码
     */
    @Schema(description = "股票编码")
    private String stockCode;
}
