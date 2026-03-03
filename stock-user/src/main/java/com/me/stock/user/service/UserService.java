package com.me.stock.user.service;

import com.me.stock.pojo.entity.SysUser;
import com.me.stock.user.dto.request.RegisterRequest;
import com.me.stock.user.dto.response.LoginResponse;

import java.util.Map;

/**
 * 用户服务接口
 *
 * @author Jovan
 * @since 1.0.0
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @param rememberMe 是否记住我
     * @return 登录响应
     */
    LoginResponse login(String username, String password, Boolean rememberMe);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 是否成功
     */
    boolean register(RegisterRequest request);

    /**
     * 用户登出
     *
     * @param token 当前 Token
     */
    void logout(String token);

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新 Token
     * @return 新的 Token 对
     */
    Map<String, String> refreshToken(String refreshToken);

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getUserByUsername(String username);

    /**
     * 根据 ID 查询用户信息
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    SysUser getUserById(Long userId);
}
