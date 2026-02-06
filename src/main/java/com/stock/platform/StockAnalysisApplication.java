package com.stock.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.ai.vectorstore.redis.RedisVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class for Stock Analysis Platform
 *
 * AI and Distributed Architecture-Based Intelligent Stock Data Analysis Platform
 * Scrape stock information from third-party websites, clean and store data,
 * build RAG knowledge base using LLM asynchronously, and implement web interface for queries.
 *
 * @author Stock Platform Team
 * @version 1.0.0
 */
@SpringBootApplication(
    scanBasePackages = "com.stock.platform",
    exclude = {
        OpenAiAutoConfiguration.class,
        RedisVectorStoreAutoConfiguration.class
    }
)
@EnableCaching
@EnableAsync
@EnableScheduling
@MapperScan("com.stock.platform.mapper")
public class StockAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockAnalysisApplication.class, args);
    }
}
