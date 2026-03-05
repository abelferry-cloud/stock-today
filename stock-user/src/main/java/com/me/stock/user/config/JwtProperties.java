package com.me.stock.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 *
 * @author stock-user
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secretKey = "stock-user-secret-key-2026-very-long-secret-key-for-security";

    /**
     * Token 过期时间（毫秒）
     * 默认 24 小时
     */
    private Long expiration = 86400000L;

    /**
     * Refresh Token 过期时间（毫秒）
     * 默认 7 天
     */
    private Long refreshExpiration = 604800000L;

    /**
     * Token 前缀
     */
    private String tokenPrefix = "Bearer ";

    /**
     * Token 请求头名称
     */
    private String header = "Authorization";
}
