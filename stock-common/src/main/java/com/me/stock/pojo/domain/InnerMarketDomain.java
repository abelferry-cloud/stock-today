package com.me.stock.pojo.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description: 定义封装多内大盘数据的实体类
 */
@Schema(description = "定义封装多内大盘数据的实体类")
@Data
public class InnerMarketDomain {
    /**
     * 大盘编码
     */
    @Schema(description = "大盘编码")
    private String code;
    /**
     * 大盘名称
     */
    @Schema(description = "大盘名称")
    private String name;
    /**
     * 开盘点
     */
    @Schema(description = "开盘点")
    private BigDecimal openPoint;
    /**
     * 当前点
     */
    @Schema(description = "当前点")
    private BigDecimal curPoint;
    /**
     * 前收盘点
     */
    @Schema(description = "前收盘点")
    private BigDecimal preClosePoint;
    /**
     * 交易量
     */
    @Schema(description = "交易量")
    private Long tradeAmt;
    /**
     * 交易金额
     */
    @Schema(description = "交易金额")
    private Long tradeVol;
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
     * 振幅
     */
    @Schema(description = "振幅")
    private BigDecimal amplitude;
    /**
     * 当前时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @Schema(description = "当前时间")
    private Date curTime;
}
