package com.me.stock.user.security;

import com.me.stock.user.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Token 提供者
 * 负责 JWT Token 的生成、解析和验证
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodeSecret(jwtProperties.getSecret()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 对 secret 进行 Base64 编码，确保长度足够
     */
    private String base64EncodeSecret(String secret) {
        // 如果 secret 不是 Base64 编码的，先进行 Base64 编码
        if (secret.length() < 32) {
            // 确保 secret 有足够的长度
            String paddedSecret = String.format("%-64s", secret).replace(' ', 'X');
            return java.util.Base64.getEncoder().encodeToString(paddedSecret.getBytes(StandardCharsets.UTF_8));
        }
        return java.util.Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 从 Token 中解析用户名
     *
     * @param token JWT Token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 从 Token 中解析过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 从 Token 中解析指定声明
     *
     * @param token JWT Token
     * @param claimsResolver 声明解析函数
     * @return 声明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从 Token 中解析所有声明
     *
     * @param token JWT Token
     * @return 声明对象
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 生成 Access Token
     *
     * @param userDetails 用户详情
     * @return JWT Token
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtProperties.getExpiration());
    }

    /**
     * 生成 Refresh Token
     *
     * @param userDetails 用户详情
     * @return JWT Token
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtProperties.getRefreshExpiration());
    }

    /**
     * 生成 Token
     *
     * @param extraClaims 额外声明
     * @param userDetails 用户详情
     * @param expiration 过期时间
     * @return JWT Token
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        final Date now = new Date();
        final Date expirationDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 验证 Token 是否有效
     *
     * @param token JWT Token
     * @param userDetails 用户详情
     * @return 是否有效
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token 已过期：{}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("不支持的 JWT Token：{}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT Token 格式不正确：{}", e.getMessage());
        } catch (SignatureException e) {
            log.error("JWT 签名验证失败：{}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT Token 为空：{}", e.getMessage());
        }
        return false;
    }

    /**
     * 检查 Token 是否过期
     *
     * @param token JWT Token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            return getExpirationDateFromToken(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * 获取 Token 剩余有效期（毫秒）
     *
     * @param token JWT Token
     * @return 剩余有效期（毫秒）
     */
    public long getRemainingExpiration(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return remaining > 0 ? remaining : 0;
        } catch (ExpiredJwtException e) {
            return 0;
        }
    }
}
