package com.me.stock.user.security;

import com.me.stock.user.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT Token 提供者
 * 负责生成、解析和验证 JWT Token
 *
 * @author stock-user
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 获取密钥
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 根据用户信息生成 Token
     *
     * @param authentication 认证信息
     * @return JWT Token
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return generateToken(username, authorities);
    }

    /**
     * 根据用户名生成 Token
     *
     * @param username 用户名
     * @param authorities 权限列表（逗号分隔）
     * @return JWT Token
     */
    public String generateToken(String username, String authorities) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(username)
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成 Refresh Token
     *
     * @param username 用户名
     * @return Refresh Token
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getRefreshExpiration());

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 Token 中获取用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    /**
     * 从 Token 中获取权限列表
     *
     * @param token JWT Token
     * @return 权限列表（逗号分隔）
     */
    public String getAuthoritiesFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("authorities", String.class);
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token 验证失败：{}", e.getMessage());
            return false;
        }
    }

    /**
     * 检查 Token 是否已过期
     *
     * @param token JWT Token
     * @return 是否已过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 解析 Claims
     *
     * @param token JWT Token
     * @return Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从 Token 中获取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration();
    }
}
