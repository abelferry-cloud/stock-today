package com.me.stock.user.security;

import com.me.stock.user.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 拦截请求，解析并验证 JWT Token
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Token 黑名单缓存 Key 前缀
     */
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中获取 Token
            String token = resolveToken(request);

            // 如果 Token 存在且有效，则设置认证信息
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token, userDetailsService.loadUserByUsername(jwtTokenProvider.getUsernameFromToken(token)))) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(jwtTokenProvider.getUsernameFromToken(token));

                // 检查 Token 是否在黑名单中
                if (isTokenInBlacklist(token)) {
                    log.debug("Token 在黑名单中，拒绝访问");
                    filterChain.doFilter(request, response);
                    return;
                }

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("设置认证信息成功，用户：{}", userDetails.getUsername());
            }
        } catch (Exception e) {
            log.error("无法设置用户认证信息：{}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中解析 Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        // 也支持从 query 参数中获取 Token
        String token = request.getParameter("token");
        if (StringUtils.hasText(token)) {
            return token;
        }
        return null;
    }

    /**
     * 检查 Token 是否在黑名单中
     * 实际实现需要结合 Redis，这里先返回 false
     */
    private boolean isTokenInBlacklist(String token) {
        // TODO: 后续结合 Redis 实现黑名单检查
        // String key = BLACKLIST_PREFIX + token;
        // return redisTemplate.hasKey(key);
        return false;
    }
}
