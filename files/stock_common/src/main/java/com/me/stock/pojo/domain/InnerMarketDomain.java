package com.me.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 定义封装多内大盘数据的实体类
 */
@ApiModel(description = "定义封装多内大盘数据的实体类")
@Data
public class InnerMarketDomain {
    /**
     * 大盘编码
     */
    @ApiModelProperty("大盘编码")
    private String code;
    /**
     * 大盘名称
     */
    @ApiModelProperty("大盘名称")
    private String name;
    /**
     * 开盘点
     */
    @ApiModelProperty("开盘点")
    private BigDecimal openPoint;
    /**
     * 当前点
     */
    @ApiModelProperty("当前点")
    private BigDecimal curPoint;
    /**
     * 前收盘点
     */
    @ApiModelProperty("前收盘点")
    private BigDecimal preClosePoint;
    /**
     * 交易量
     */
    @ApiModelProperty("交易量")
    private Long tradeAmt;
    /**
     * 交易金额
     */
    @ApiModelProperty("交易金额")
    private Long tradeVol;
    /**
     * 涨跌值
     */
    @ApiModelProperty("涨跌值")
    private BigDecimal upDown;

    /**
     * 涨幅
     */
    @ApiModelProperty("涨幅")
    private BigDecimal rose;

    /**
     * 振幅
     */
    @ApiModelProperty("振幅")
    private BigDecimal amplitude;
    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty("当前时间")
    private Date curTime;
}