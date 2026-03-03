package com.me.stock.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 *
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥
     */
    private String secret;

    /**
     * Access Token 过期时间（毫秒），默认 2 小时
     */
    private Long expiration = 7200000L;

    /**
     * Refresh Token 过期时间（毫秒），默认 7 天
     */
    private Long refreshExpiration = 604800000L;

    /**
     * Token 前缀
     */
    private String tokenHeader = "Authorization";

    /**
     * Token 前缀格式
     */
    private String tokenPrefix = "Bearer ";
}
