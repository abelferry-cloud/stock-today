package com.me.stock.pojo.vo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 股票信息配置
 * 从application.yml中读取股票相关配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "stock")
public class StockInfoConfig {

    /**
     * 大盘URL
     */
    private String marketURL;

    /**
     * 国内大盘代码列表
     */
    private List<String> inner;

    /**
     * 国外大盘代码列表
     */
    private List<String> outer;

    /**
     * 股票代码列表
     */
    private List<String> stocks;

    /**
     * 板块URL
     */
    private String blockURL;
}
