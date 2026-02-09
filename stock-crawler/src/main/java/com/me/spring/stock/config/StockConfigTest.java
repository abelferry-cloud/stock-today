package com.me.spring.stock.config;

import com.me.stock.pojo.vo.StockInfoConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 股票配置测试类
 * 用于验证配置是否正确加载
 */
@Component
@Slf4j
public class StockConfigTest implements CommandLineRunner {

    @Autowired
    private StockInfoConfig stockInfoConfig;

    @Override
    public void run(String... args) throws Exception {
        log.info("=== 股票配置加载测试 ===");
        log.info("Market URL: {}", stockInfoConfig.getMarketURL());
        log.info("Block URL: {}", stockInfoConfig.getBlockURL());
        log.info("Inner markets: {}", stockInfoConfig.getInner());
        log.info("Outer markets: {}", stockInfoConfig.getOuter());
        log.info("Stocks: {}", stockInfoConfig.getStocks());
        log.info("=== 配置测试完成 ===");
    }
}