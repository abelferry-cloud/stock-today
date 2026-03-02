package com.me.spring.stockanalysisai.pojo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 股票K线数据响应VO
 *
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "股票K线数据")
public class StockKLineResponseVO {

    /**
     * 股票代码
     */
    @Schema(description = "股票代码")
    private String stockCode;

    /**
     * 股票名称
     */
    @Schema(description = "股票名称")
    private String stockName;

    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "当前时间", example = "2024-01-01 09:30:00")
    private Date curTime;

    /**
     * 开盘价
     */
    @Schema(description = "开盘价")
    private BigDecimal openPrice;

    /**
     * 收盘价
     */
    @Schema(description = "收盘价")
    private BigDecimal closePrice;

    /**
     * 最高价
     */
    @Schema(description = "最高价")
    private BigDecimal maxPrice;

    /**
     * 最低价
     */
    @Schema(description = "最低价")
    private BigDecimal minPrice;

    /**
     * 成交量
     */
    @Schema(description = "成交量")
    private BigDecimal tradeVol;

    /**
     * 成交金额
     */
    @Schema(description = "成交金额")
    private Long tradeAmt;
}