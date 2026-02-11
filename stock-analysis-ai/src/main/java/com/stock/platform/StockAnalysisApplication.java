package com.stock.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.vectorstore.pinecone.autoconfigure.PineconeVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AI股票数据分析平台 - 主启动类
 */
@SpringBootApplication(exclude = PineconeVectorStoreAutoConfiguration.class)
@ComponentScan(basePackages = {"com.stock.platform", "com.me.stock"})
@EnableAsync
@EnableScheduling
@MapperScan("com.stock.platform.mapper")
public class StockAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockAnalysisApplication.class, args);
    }

}
