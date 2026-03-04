package com.me.stock.pojo.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个股详情信息表
 * @TableName stock_rt_info
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRtInfo {
    /**
     * 主键字段（无业务意义）
     */
    private Long id;

    /**
     * 股票代码
     */
    private String stockCode;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 前收盘价| 昨日收盘价
     */
    private BigDecimal preClosePrice;

    /**
     * 开盘价
     */
    private BigDecimal openPrice;

    /**
     * 当前价格
     */
    private BigDecimal curPrice;

    /**
     * 今日最低价
     */
    private BigDecimal minPrice;

    /**
     * 今日最高价
     */
    private BigDecimal maxPrice;

    /**
     * 成交量
     */
    private Long tradeAmount;

    /**
     * 成交金额
     */
    private BigDecimal tradeVolume;

    /**
     * 当前时间
     */
    private Date curTime;
}