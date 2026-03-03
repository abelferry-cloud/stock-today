package com.me.stock.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 用户中心模块启动类
 * 提供用户认证、授权、权限管理等功能
 *
 * @author Jovan
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.me.stock.mapper")
public class StockUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockUserApplication.class, args);
    }
}
