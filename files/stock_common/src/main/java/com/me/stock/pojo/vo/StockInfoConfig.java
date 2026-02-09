package com.me.stock.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * 获取股票信息配置
 */
@ApiModel(description = "获取股票信息配置")
@ConfigurationProperties(prefix = "stock")
@Data
public class StockInfoConfig {
    /**
     * A股大盘ID集合
     */
    @ApiModelProperty(value = "A股大盘ID集合")
    private List<String> inner;
    /*
    * 外盘ID集合
     */
    @ApiModelProperty(value = "外盘ID集合")
    private List<String> outer;
    /**
     * 涨跌幅范围
     */
    @ApiModelProperty(value = "涨跌幅范围")
    private List<String> upDownRange;

    /**
     * 大盘、外盘公共的获取股票信息URL 详情参见：<a href="http://hq.sinajs.cn/list=sh000001">...</a>
     */
    @ApiModelProperty(value = "大盘、外盘公共的URL")
    private String marketURL;
    /**
     * 板块URL 详情参见：<a href="http://vip.stock.finance.sina.com.cn/q/view/newSinaHy.php">...</a>
     */
    @ApiModelProperty(value = "板块URL")
    private String blockURL;
}