package com.me.spring.stockanalysisai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.me.spring.stockanalysisai",
        "com.me.stock"
})
public class StockAnalysisAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockAnalysisAiApplication.class, args);
    }

}

