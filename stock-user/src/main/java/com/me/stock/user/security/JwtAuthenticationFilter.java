package com.me.stock.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * JWT 认证过滤器
 * 拦截请求，解析 Token，设置认证信息
 *
 * @author stock-user
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求中获取 Token
            String token = resolveToken(request);

            // 如果 Token 存在且有效，设置认证信息
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                String username = jwtTokenProvider.getUsernameFromToken(token);
                String authoritiesStr = jwtTokenProvider.getAuthoritiesFromToken(token);

                // 解析权限列表
                Collection<SimpleGrantedAuthority> authorities = Arrays.stream(authoritiesStr.split(","))
                        .filter(StringUtils::hasText)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                // 设置认证信息到 SecurityContext
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("设置认证信息：username={}, authorities={}", username, authorities);
            }
        } catch (Exception e) {
            log.error("JWT 认证失败：{}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中解析 Token
     */
    private String resolveToken(HttpServletRequest request) {
        // 从 Header 中获取
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        // 从请求参数中获取
        String token = request.getParameter("token");
        if (StringUtils.hasText(token)) {
            return token;
        }

        return null;
    }
}
