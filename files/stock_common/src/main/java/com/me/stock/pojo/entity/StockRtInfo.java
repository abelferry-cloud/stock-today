package com.me.stock.pojo.entity;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个股详情信息表
 * @TableName stock_rt_info
 */
@ApiModel(description = "个股实时信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRtInfo {
    /**
     * 主键字段（无业务意义）
     */
    @ApiModelProperty(value = "主键ID", example = "1")
    private Long id;

    /**
     * 股票代码
     */
    @ApiModelProperty(value = "股票代码", example = "000001")
    private String stockCode;

    /**
     * 股票名称
     */
    @ApiModelProperty(value = "股票名称", example = "平安银行")
    private String stockName;

    /**
     * 前收盘价| 昨日收盘价
     */
    @ApiModelProperty(value = "前收盘价", example = "12.50")
    private BigDecimal preClosePrice;

    /**
     * 开盘价
     */
    @ApiModelProperty(value = "开盘价", example = "12.60")
    private BigDecimal openPrice;

    /**
     * 当前价格
     */
    @ApiModelProperty(value = "当前价格", example = "12.75")
    private BigDecimal curPrice;

    /**
     * 今日最低价
     */
    @ApiModelProperty(value = "今日最低价", example = "12.40")
    private BigDecimal minPrice;

    /**
     * 今日最高价
     */
    @ApiModelProperty(value = "今日最高价", example = "12.90")
    private BigDecimal maxPrice;

    /**
     * 成交量
     */
    @ApiModelProperty(value = "成交量（手）", example = "1000000")
    private Long tradeAmount;

    /**
     * 成交金额
     */
    @ApiModelProperty(value = "成交金额（元）", example = "12750000.00")
    private BigDecimal tradeVolume;

    /**
     * 当前时间
     */
    @ApiModelProperty(value = "当前时间")
    private Date curTime;
}