package com.me.spring;

import com.me.stock.config.RabbitMQProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@SpringBootApplication
@MapperScan("com.me.stock.mapper")
@EnableConfigurationProperties(RabbitMQProperties.class)
public class jobApplication {
    public static void main(String[] args) {
        SpringApplication.run(jobApplication.class, args);
    }
}
