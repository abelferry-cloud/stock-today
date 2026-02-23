package com.me.spring.stockanalysisai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS跨域配置类
 * 用于配置允许跨域请求的相关参数
 * 
 * @author system
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * 配置CORS过滤器
     * 
     * @return CorsFilter实例
     */
    @Bean
    public CorsFilter corsFilter() {
        // 创建CorsConfiguration对象
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的域名来源 - 生产环境建议指定具体域名
        config.addAllowedOriginPattern("*");
        
        // 允许的请求头
        config.addAllowedHeader("*");
        
        // 允许的请求方法
        config.addAllowedMethod("*");
        
        // 是否允许携带凭证信息（如cookies）
        config.setAllowCredentials(true);
        
        // 预检请求的有效期（秒）
        config.setMaxAge(3600L);
        
        // 创建UrlBasedCorsConfigurationSource对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        // 对所有路径应用CORS配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}