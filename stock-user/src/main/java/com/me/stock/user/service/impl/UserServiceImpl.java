package com.me.stock.user.service.impl;

import com.me.stock.mapper.SysUserMapper;
import com.me.stock.pojo.domain.SysUserDomain;
import com.me.stock.pojo.entity.SysUser;
import com.me.stock.pojo.vo.UserMConditionReqVO;
import com.me.stock.user.common.ResultCode;
import com.me.stock.user.config.JwtProperties;
import com.me.stock.user.dto.request.RegisterRequest;
import com.me.stock.user.dto.request.UserInfoRequest;
import com.me.stock.user.dto.response.LoginResponse;
import com.me.stock.user.security.JwtTokenProvider;
import com.me.stock.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Token 黑名单 Key 前缀
     */
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    /**
     * 用户登录
     */
    @Override
    public LoginResponse login(String username, String password, Boolean rememberMe) {
        // 查询用户信息
        SysUser user = sysUserMapper.findByUserName(username);
        if (user == null) {
            log.warn("用户不存在：{}", username);
            throw new RuntimeException("用户名或密码错误");
        }

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("密码错误：{}", username);
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() != 1) {
            log.warn("用户已被禁用：{}", username);
            throw new RuntimeException("用户已被禁用");
        }

        // 生成 Token
        UserDetails userDetails = User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // 如果选择记住我，延长 Token 有效期
        if (Boolean.TRUE.equals(rememberMe)) {
            // TODO: 实现记住我功能，可以延长 Token 有效期
        }

        // 构建用户信息
        LoginResponse.UserInfoDTO userInfo = LoginResponse.UserInfoDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickName(user.getNickName())
                .phone(maskPhone(user.getPhone()))
                .email(user.getEmail())
                .build();

        log.info("用户登录成功：{}", username);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .userInfo(userInfo)
                .build();
    }

    /**
     * 用户注册
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(RegisterRequest request) {
        // 校验密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否存在
        SysUser existUser = sysUserMapper.findByUserName(request.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 构建用户实体
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setNickName(request.getNickName() != null ? request.getNickName() : request.getUsername());
        user.setStatus(1);
        user.setDeleted(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 插入数据库
        int result = sysUserMapper.addUser(user);
        if (result <= 0) {
            throw new RuntimeException("注册失败");
        }

        log.info("用户注册成功：{}", request.getUsername());
        return true;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout(String token) {
        if (StringUtils.hasText(token)) {
            // 将 Token 加入黑名单
            long remainingTime = jwtTokenProvider.getRemainingExpiration(token);
            if (remainingTime > 0) {
                String key = BLACKLIST_PREFIX + token;
                redisTemplate.opsForValue().set(key, "logout", remainingTime, TimeUnit.MILLISECONDS);
                log.debug("Token 已加入黑名单");
            }
        }
        log.info("用户登出成功");
    }

    /**
     * 刷新 Token
     */
    @Override
    public Map<String, String> refreshToken(String refreshToken) {
        try {
            // 验证 Refresh Token 有效性
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            SysUser user = sysUserMapper.findByUserName(username);

            if (user == null) {
                throw new RuntimeException("用户不存在");
            }

            UserDetails userDetails = User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(new ArrayList<>())
                    .build();

            if (!jwtTokenProvider.validateToken(refreshToken, userDetails)) {
                throw new RuntimeException("Refresh Token 无效");
            }

            // 生成新的 Token
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);

            log.info("Token 刷新成功：{}", username);
            return tokens;
        } catch (Exception e) {
            log.error("Token 刷新失败：{}", e.getMessage());
            throw new RuntimeException("Token 刷新失败");
        }
    }

    /**
     * 根据用户名查询用户信息
     */
    @Override
    public SysUser getUserByUsername(String username) {
        return sysUserMapper.findByUserName(username);
    }

    /**
     * 根据 ID 查询用户信息
     */
    @Override
    public SysUser getUserById(Long userId) {
        return sysUserMapper.selectByPrimaryKey(userId);
    }

    /**
     * 脱敏手机号
     */
    private String maskPhone(String phone) {
        if (!StringUtils.hasText(phone) || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 根据用户名查询用户信息（返回 Domain）
     */
    @Override
    public SysUserDomain getUserInfoByUsername(String username) {
        SysUser user = sysUserMapper.findUserInfoByUserName(username);
        if (user == null) {
            return null;
        }
        // 转换为 SysUserDomain
        SysUserDomain domain = new SysUserDomain();
        domain.setId(user.getId());
        domain.setUsername(user.getUsername());
        domain.setPassword(user.getPassword());
        domain.setPhone(user.getPhone());
        domain.setRealName(user.getRealName());
        domain.setNickName(user.getNickName());
        domain.setEmail(user.getEmail());
        domain.setStatus(user.getStatus());
        domain.setSex(user.getSex());
        domain.setDeleted(user.getDeleted());
        domain.setCreateId(user.getCreateId());
        domain.setUpdateId(user.getUpdateId());
        domain.setCreateWhere(user.getCreateWhere());
        domain.setCreateTime(user.getCreateTime());
        domain.setUpdateTime(user.getUpdateTime());
        return domain;
    }

    /**
     * 根据 ID 查询用户信息（返回 Domain）
     */
    @Override
    public SysUserDomain getUserInfoById(Long userId) {
        return null; // getUserInfoById 返回的是 UserInfoVO，需要另外处理
    }

    /**
     * 条件查询用户列表
     */
    @Override
    public List<SysUserDomain> listUsersByCondition(UserMConditionReqVO reqVO) {
        Date startTime = reqVO.getStartTime();
        Date endTime = reqVO.getEndTime();
        return sysUserMapper.getUsersInfoByMCondition(
                reqVO.getUserName(),
                reqVO.getNickName(),
                startTime,
                endTime
        );
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, UserInfoRequest request) {
        SysUser user = sysUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setNickName(request.getNickName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setSex(request.getSex());
        user.setUpdateTime(new Date());

        int result = sysUserMapper.updateUserInfo(user);
        if (result <= 0) {
            throw new RuntimeException("更新用户信息失败");
        }

        log.info("更新用户信息成功：{}", userId);
    }

    /**
     * 修改密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String username, String oldPassword, String newPassword) {
        SysUser user = sysUserMapper.findByUserName(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(new Date());

        int result = sysUserMapper.updateUserInfo(user);
        if (result <= 0) {
            throw new RuntimeException("修改密码失败");
        }

        log.info("修改密码成功：{}", username);
    }
}
