package com.me.stock.user.service;

import com.me.stock.pojo.entity.SysUser;
import com.me.stock.user.dto.request.LoginRequest;
import com.me.stock.user.dto.request.RegisterRequest;
import com.me.stock.user.dto.response.LoginResponse;
import com.me.stock.user.dto.response.UserVO;

/**
 * 用户服务接口
 *
 * @author stock-user
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    UserVO register(RegisterRequest request);

    /**
     * 刷新 Token
     *
     * @param refreshToken 刷新 Token
     * @return 新的 Token 信息
     */
    LoginResponse refreshToken(String refreshToken);

    /**
     * 获取当前用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    UserVO getUserInfo(String username);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getUserByUsername(String username);

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户实体
     */
    SysUser getUserById(Long id);

    /**
     * 更新当前登录用户信息
     *
     * @param username 用户名
     * @param request 更新请求参数
     * @return 更新后的用户信息
     */
    UserVO updateUserProfile(String username, com.me.stock.user.dto.request.UpdateProfileRequest request);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(SysUser user);

    /**
     * 更新最后登录时间
     *
     * @param userId 用户 ID
     */
    void updateLastLoginTime(Long userId);
}
