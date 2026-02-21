package com.me.spring.stockanalysisai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication(
        scanBasePackages = {
                "com.me.spring.stockanalysisai",
                "com.me.stock"
        }
)
@EnableRabbit
public class StockAnalysisAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockAnalysisAiApplication.class, args);
    }

}

