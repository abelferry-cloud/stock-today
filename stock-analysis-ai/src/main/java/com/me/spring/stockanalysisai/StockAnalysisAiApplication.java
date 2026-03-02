package com.me.spring.stockanalysisai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(
        scanBasePackages = {
                "com.me.spring.stockanalysisai",
                "com.me.stock"
        }
)
@EnableRabbit
@MapperScan("com.me.stock.mapper")
@EnableCaching
public class StockAnalysisAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockAnalysisAiApplication.class, args);
    }

}

