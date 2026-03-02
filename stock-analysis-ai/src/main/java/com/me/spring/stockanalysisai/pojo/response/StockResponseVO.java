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
 * 股票数据响应VO
 *
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "股票数据响应")
public class StockResponseVO {

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
     * 当前价格
     */
    @Schema(description = "当前价格")
    private BigDecimal tradePrice;

    /**
     * 前收盘价
     */
    @Schema(description = "前收盘价")
    private BigDecimal preClosePrice;

    /**
     * 涨跌值
     */
    @Schema(description = "涨跌值")
    private BigDecimal upDown;

    /**
     * 涨跌幅
     */
    @Schema(description = "涨跌幅")
    private BigDecimal increase;

    /**
     * 振幅
     */
    @Schema(description = "振幅")
    private BigDecimal amplitude;

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

    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "当前时间")
    private Date curDate;

    // ========== 大盘指数相关字段 ==========

    /**
     * 大盘编码
     */
    @Schema(description = "大盘编码")
    private String marketCode;

    /**
     * 大盘名称
     */
    @Schema(description = "大盘名称")
    private String marketName;

    // ========== 板块相关字段 ==========

    /**
     * 板块编码
     */
    @Schema(description = "板块编码")
    private String blockCode;

    /**
     * 板块名称
     */
    @Schema(description = "板块名称")
    private String blockName;

    /**
     * 板块内公司数量
     */
    @Schema(description = "板块内公司数量")
    private Integer companyNum;

    // ========== 主营业务相关字段 ==========

    /**
     * 主营业务
     */
    @Schema(description = "主营业务")
    private String mainBusiness;
}