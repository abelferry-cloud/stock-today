package com.me.stock.pojo.domain;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 定义外部指数数据实体类
 */
@Data
@ApiOperation(value = "定义外部指数数据实体类")
public class ExternalIndexDomain {
    /**
     * 指数名称
     */
    @ApiModelProperty("大盘名称")
    private String name;

    /**
     * 指数点
     */
    @ApiModelProperty("指数点")
    private BigDecimal curPoint;

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
     * 当前时间
     */
    @ApiModelProperty("当前时间")
    private String curTime;
}
