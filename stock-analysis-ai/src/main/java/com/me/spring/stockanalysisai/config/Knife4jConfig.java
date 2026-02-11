package com.me.spring.stockanalysisai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 配置类
 * 用于配置 API 文档的展示信息
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock Analysis AI API")
                        .version("1.0.0")
                        .description("基于 SpringAI 和 DeepSeek 的智能股票分析助手 API 文档")
                        .contact(new Contact()
                                .name("Stock Today Team")
                                .email("support@stock-today.com")
                                .url("https://github.com/stock-today"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
