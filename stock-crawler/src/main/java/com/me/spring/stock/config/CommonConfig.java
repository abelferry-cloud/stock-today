package com.me.spring.stock.config;

import com.me.stock.pojo.vo.StockInfoConfig;
import com.me.stock.utils.IdWorker;
import com.me.stock.utils.ParserStockInfoUtil;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(StockInfoConfig.class)
public class CommonConfig {

    /**
     * 雪花算法：配置id生成器bean
     * @return IdWorker
     */
    @Bean
    public IdWorker idWorker(){
        /**
         * 1.workerId 机器ID
         * 2.datacenterId 机房编号
         */
        //基于运维人员对机房和机器的编号规划自行约定
        return new IdWorker(1L, 2L);
    }

    /**
     * 正则解析股票，大盘，外盘，A股，板块等信息
     * @param idWorker id生成器
     * @return 解析工具类
     */
    @Bean
    public ParserStockInfoUtil parserStockInfoUtil(IdWorker idWorker){
        return new ParserStockInfoUtil(idWorker);
    }
}