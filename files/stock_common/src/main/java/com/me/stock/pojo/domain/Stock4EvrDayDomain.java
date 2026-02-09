package com.me.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(description = "个股日K数据")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock4EvrDayDomain {
    /**
     * 日期，eg:202201280809
     */
   @JsonFormat(pattern = "yyyy-MM-dd HH:mm",timezone = "Asia/Shanghai")
   @ApiModelProperty("日期")
   private Date date;
    /**
     * 交易量
     */
    @ApiModelProperty("交易量")
   private Long tradeAmt;
    /**
     * 股票编码
     */
    @ApiModelProperty("股票编码")
   private String code;
    /**
     * 最低价
     */
    @ApiModelProperty("最低价")
   private BigDecimal lowPrice;
    /**
     * 股票名称
     */
    @ApiModelProperty("股票名称")
    private String name;
    /**
     * 最高价
     */
    @ApiModelProperty("最高价")
    private BigDecimal highPrice;
    /**
     * 开盘价
     */
    @ApiModelProperty("开盘价")
    private BigDecimal openPrice;
    /**
     * 当前交易总金额
     */
    @ApiModelProperty("当前交易总金额")
    private BigDecimal tradeVol;
    /**
     * 当前收盘价格指收盘时的价格，如果当天未收盘，则显示最新cur_price）
     */
    @ApiModelProperty("当前收盘价格指收盘时的价格")
    private BigDecimal closePrice;
    /**
     * 前收盘价
     */
    @ApiModelProperty("前收盘价")
   private BigDecimal preClosePrice;
}