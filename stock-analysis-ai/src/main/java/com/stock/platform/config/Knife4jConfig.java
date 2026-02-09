package com.stock.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Knife4j配置类
 * 配置API文档信息
 */
@Configuration
public class Knife4jConfig {

    /**
     * OpenAPI基本信息配置
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("股票智能分析平台 API")
                        .version("1.0.0")
                        .description("基于AI和分布式架构的智能股票数据分析平台API文档")
                        .contact(new Contact()
                                .name("Stock Platform Team")
                                .email("support@stock-platform.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("本地开发环境"),
                        new Server().url("https://api.stock-platform.com").description("生产环境")
                ));
    }

    /**
     * 聊天模块API分组
     */
    @Bean
    public GroupedOpenApi chatApi() {
        return GroupedOpenApi.builder()
                .group("01-聊天模块")
                .pathsToMatch("/chat/**")
                .build();
    }

    /**
     * 对话记录模块API分组
     */
    @Bean
    public GroupedOpenApi conversationApi() {
        return GroupedOpenApi.builder()
                .group("02-对话记录模块")
                .pathsToMatch("/conversations/**")
                .build();
    }

    /**
     * 监控模块API分组
     */
    @Bean
    public GroupedOpenApi monitorApi() {
        return GroupedOpenApi.builder()
                .group("03-监控模块")
                .pathsToMatch("/monitor/**")
                .build();
    }
}
