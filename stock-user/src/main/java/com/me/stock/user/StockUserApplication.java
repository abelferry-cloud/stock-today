package com.me.stock.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 用户中心模块启动类
 * 提供 JWT 认证、用户管理、权限控制等功能
 *
 * @author stock-user
 */
@SpringBootApplication
@MapperScan("com.me.stock.mapper")
public class StockUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockUserApplication.class, args);
    }
}
