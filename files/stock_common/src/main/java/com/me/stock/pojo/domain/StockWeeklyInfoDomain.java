package com.me.stock.pojo.domain;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
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
@ApiOperation(value = "每周股票信息")
public class StockWeeklyInfoDomain {

    /**
     * 平均价格
     */
    @ApiModelProperty("平均价格")
    private BigDecimal avgPrice;

    /**
     * 最低价
     */
    @ApiModelProperty("最低价")
    private BigDecimal minPrice;

    /**
     * 开盘价
     */
    @ApiModelProperty("开盘价")
    private BigDecimal openPrice;

    /**
     * 最高价
     */
    @ApiModelProperty("最高价")
    private BigDecimal maxPrice;

    /**
     * 收盘价
     */
    @ApiModelProperty("收盘价")
    private BigDecimal closePrice;

    /**
     * 最高时间
     */
    @ApiModelProperty("最高时间")
    private Date maxTime;

    /**
     * 股票编码
     */
    @ApiModelProperty("股票编码")
    private String stockCode;
}
