package com.me.stock.pojo.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiOperation(value = "股票信息")
public class StockUpdownDomain {

    /**
     * 股票代码
     */
    @ApiModelProperty("股票代码")
    @ExcelProperty(value = "股票代码", index = 0)
    private String code;

    /**
     * 股票名称
     */
    @ApiModelProperty("股票名称")
    @ExcelProperty(value = "股票名称", index = 1)
    private String name;

    /**
     * 前收盘价
     */
    @ApiModelProperty("前收盘价")
    @ExcelProperty(value = "前收盘价", index = 2)
    private BigDecimal preClosePrice;

    /**
     * 当前价格
     */
    @ApiModelProperty("当前价格")
    @ExcelProperty(value = "当前价格", index = 3)
    private BigDecimal tradePrice;

    /**
     * 涨幅
     */
    @ExcelProperty(value = "涨幅", index = 4)
    @ApiModelProperty("涨幅")
    private BigDecimal increase;

    /**
     * 涨跌值
     */
    @ExcelProperty(value = "涨跌值", index = 5)
    @ApiModelProperty("涨跌值")
    private BigDecimal upDown;

    /**
     * 振幅
     */
    @ExcelProperty(value = "振幅", index = 6)
    @ApiModelProperty("振幅")
    private BigDecimal amplitude;

    /**
     * 交易金额
     */
    @ApiModelProperty("交易金额")
    @ExcelProperty(value = "交易金额", index = 7)
    private Long tradeAmt;

    /**
     * 交易量
     */
    @ApiModelProperty("交易量")
    @ExcelProperty(value = "交易量", index = 8)
    private BigDecimal tradeVol;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("日期")
    @ExcelProperty(value = "日期", index = 9)
    @DateTimeFormat(value = "yyyy-MM-dd")
    private Date curDate;
}