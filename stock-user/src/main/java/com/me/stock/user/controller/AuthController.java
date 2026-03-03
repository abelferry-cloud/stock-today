package com.me.stock.user.controller;

import com.me.stock.user.common.Result;
import com.me.stock.user.dto.request.LoginRequest;
import com.me.stock.user.dto.request.RegisterRequest;
import com.me.stock.user.dto.request.TokenRefreshRequest;
import com.me.stock.user.dto.response.LoginResponse;
import com.me.stock.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证接口控制器
 * 处理用户登录、注册、登出、刷新 Token 等请求
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "认证接口")
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户名密码登录，返回 Access Token 和 Refresh Token")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request.getUsername(), request.getPassword(), request.getRememberMe());
        return Result.success(response);
    }

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册结果
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户")
    public Result<Boolean> register(@Valid @RequestBody RegisterRequest request) {
        boolean result = userService.register(request);
        return Result.success(result);
    }

    /**
     * 用户登出
     *
     * @param request HTTP 请求
     * @return 登出结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，Token 加入黑名单")
    public Result<Void> logout(HttpServletRequest request) {
        String token = resolveToken(request);
        userService.logout(token);
        return Result.success(null);
    }

    /**
     * 刷新 Token
     *
     * @param request 刷新 Token 请求
     * @return 新的 Token 对
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新 Token", description = "使用 Refresh Token 获取新的 Access Token")
    public Result<Map<String, String>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        Map<String, String> tokens = userService.refreshToken(request.getRefreshToken());
        return Result.success(tokens);
    }

    /**
     * 获取当前登录用户信息
     *
     * @param userDetails 当前认证用户
     * @return 用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserDetails> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return Result.success(userDetails);
    }

    /**
     * 从请求头中解析 Token
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
