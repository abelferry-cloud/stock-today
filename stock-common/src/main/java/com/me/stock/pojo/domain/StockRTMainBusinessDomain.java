package com.me.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "个股主营业务")
@Data
public class StockRTMainBusinessDomain {
    /**
     * 股票编码
     */
    @Schema(description = "股票编码")
    private String code;

    /**
     * 行业名称
     */
    @Schema(description = "行业名称")
    private String trade;

    /**
     * 公司主营业务业务名称
     */
    @Schema(description = "公司主营业务业务名称")
    private String business;

    /**
     * 公司名称
     */
    @Schema(description = "公司名称")
    private String name;
}
