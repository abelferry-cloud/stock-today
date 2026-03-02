package com.me.spring.stockanalysisai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j配置类
 * 用于配置API文档的展示信息
 * 
 * @author Jovan
 * @since 1.0.0
 */
@Configuration
public class Knife4jConfig {

    /**
     * 配置OpenAPI文档信息
     * 
     * @return OpenAPI实例
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock Analysis AI API")
                        .version("1.0.0")
                        .description("基于Spring AI和DeepSeek的智能股票分析助手API文档")
                        .contact(new Contact()
                                .name("Jovan")
                                .email("2120814280@qq.com")
                                .url("https://github.com/stock-today"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
