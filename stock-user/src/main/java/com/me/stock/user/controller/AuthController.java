package com.me.stock.user.controller;

import com.me.stock.user.common.Result;
import com.me.stock.user.dto.request.LoginRequest;
import com.me.stock.user.dto.request.RegisterRequest;
import com.me.stock.user.dto.request.TokenRefreshRequest;
import com.me.stock.user.dto.response.LoginResponse;
import com.me.stock.user.dto.response.UserVO;
import com.me.stock.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、注册、刷新 Token 等认证相关请求
 *
 * @author stock-user
 */
@Slf4j
@Tag(name = "认证管理", description = "用户登录、注册、刷新 Token 等认证相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "用户登录", description = "用户名密码登录，返回 Access Token 和 Refresh Token")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录：username={}", request.getUsername());
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    @Operation(summary = "用户注册", description = "注册新用户")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册：username={}", request.getUsername());
        UserVO userVO = userService.register(request);
        return Result.success("注册成功", userVO);
    }

    @Operation(summary = "刷新 Token", description = "使用 Refresh Token 获取新的 Access Token")
    @PostMapping("/refresh")
    public Result<LoginResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        log.info("刷新 Token");
        LoginResponse response = userService.refreshToken(request.getRefreshToken());
        return Result.success(response);
    }

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser(@AuthenticationPrincipal String username) {
        log.info("获取当前用户信息：username={}", username);
        UserVO userVO = userService.getUserInfo(username);
        return Result.success(userVO);
    }

    @Operation(summary = "登出", description = "用户登出（前端删除 Token 即可）")
    @PostMapping("/logout")
    public Result<Void> logout() {
        log.info("用户登出");
        return Result.success();
    }
}
