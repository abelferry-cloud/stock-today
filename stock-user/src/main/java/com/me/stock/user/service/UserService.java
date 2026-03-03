package com.me.stock.user.service;

import com.me.stock.pojo.domain.SysUserDomain;
import com.me.stock.pojo.entity.SysUser;
import com.me.stock.pojo.vo.UserMConditionReqVO;
import com.me.stock.user.dto.request.RegisterRequest;
import com.me.stock.user.dto.request.UserInfoRequest;
import com.me.stock.user.dto.response.LoginResponse;

import java.util.List;
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

    /**
     * 根据用户名查询用户信息（返回 Domain）
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUserDomain getUserInfoByUsername(String username);

    /**
     * 根据 ID 查询用户信息（返回 Domain）
     *
     * @param userId 用户 ID
     * @return 用户信息
     */
    SysUserDomain getUserInfoById(Long userId);

    /**
     * 条件查询用户列表
     *
     * @param reqVO 查询条件
     * @return 用户列表
     */
    List<SysUserDomain> listUsersByCondition(UserMConditionReqVO reqVO);

    /**
     * 更新用户信息
     *
     * @param userId 用户 ID
     * @param request 更新请求
     */
    void updateUserInfo(Long userId, UserInfoRequest request);

    /**
     * 修改密码
     *
     * @param username 用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(String username, String oldPassword, String newPassword);
}
