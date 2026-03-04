package com.me.stock.pojo.entity;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外盘详情信息表
 * @TableName stock_outer_market_index_info
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockOuterMarketIndexInfo {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 大盘编码
     */
    private String marketCode;

    /**
     * 大盘名称
     */
    private String marketName;

    /**
     * 大盘当前点
     */
    private BigDecimal curPoint;

    /**
     * 大盘涨跌值
     */
    private BigDecimal updown;

    /**
     * 大盘涨幅
     */
    private BigDecimal rose;

    /**
     * 当前时间
     */
    private Date curTime;
}