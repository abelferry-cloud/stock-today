package com.me.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 定义外部指数数据实体类
 */
@Data
@Schema(description = "定义外部指数数据实体类")
public class ExternalIndexDomain {
    /**
     * 指数名称
     */
    @Schema(description = "大盘名称")
    private String name;

    /**
     * 指数点
     */
    @Schema(description = "指数点")
    private BigDecimal curPoint;

    /**
     * 涨跌值
     */
    @Schema(description = "涨跌值")
    private BigDecimal upDown;

    /**
     * 涨幅
     */
    @Schema(description = "涨幅")
    private BigDecimal rose;

    /**
     * 当前时间
     */
    @Schema(description = "当前时间")
    private String curTime;
}
