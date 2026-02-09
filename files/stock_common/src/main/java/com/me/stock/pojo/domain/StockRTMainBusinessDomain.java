package com.me.stock.pojo.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "个股主营业务")
@Data
public class StockRTMainBusinessDomain {
    /**
     * 股票编码
     */
    @ApiModelProperty("股票编码")
    private String code;

    /**
     * 行业名称
     */
    @ApiModelProperty("行业名称")
    private String trade;

    /**
     * 公司主营业务业务名称
     */
    @ApiModelProperty("公司主营业务业务名称")
    private String business;

    /**
     * 公司名称
     */
    @ApiModelProperty("公司名称")
    private String name;
}
