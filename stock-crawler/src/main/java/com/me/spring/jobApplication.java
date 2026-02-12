package com.me.spring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("com.me.stock.mapper")
public class jobApplication {
    public static void main(String[] args) {
        SpringApplication.run(jobApplication.class, args);
    }
}
