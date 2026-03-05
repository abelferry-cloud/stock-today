package com.me.stock.user.service.impl;

import com.me.stock.mapper.SysRoleMapper;
import com.me.stock.mapper.SysUserMapper;
import com.me.stock.pojo.entity.SysRole;
import com.me.stock.pojo.entity.SysUser;
import com.me.stock.user.common.ResultCode;
import com.me.stock.user.dto.request.LoginRequest;
import com.me.stock.user.dto.request.RegisterRequest;
import com.me.stock.user.dto.response.LoginResponse;
import com.me.stock.user.dto.response.UserVO;
import com.me.stock.user.security.JwtTokenProvider;
import com.me.stock.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 用户服务实现类
 *
 * @author stock-user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录：username={}", request.getUsername());

        try {
            // Spring Security 认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 生成 Token
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(request.getUsername());

            // 获取用户信息
            SysUser user = sysUserMapper.findUserInfoByUserName(request.getUsername());

            // 更新最后登录时间 (使用数据库字段 update_time 记录)
            if (user != null) {
                SysUser updateEntity = new SysUser();
                updateEntity.setId(user.getId());
                updateEntity.setUpdateTime(new Date());
                sysUserMapper.updateByPrimaryKeySelective(updateEntity);
            }

            // 构建响应
            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(86400000L)
                    .user(convertToVO(user))
                    .build();

        } catch (DisabledException e) {
            log.warn("用户已被禁用：username={}", request.getUsername());
            throw new RuntimeException("用户已被禁用");
        } catch (BadCredentialsException e) {
            log.warn("用户名或密码错误：username={}", request.getUsername());
            throw new RuntimeException("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录失败：username={}, error={}", request.getUsername(), e.getMessage());
            throw new RuntimeException("登录失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(RegisterRequest request) {
        log.info("用户注册：username={}, phone={}, email={}",
                request.getUsername(), request.getPhone(), request.getEmail());

        // 检查用户名是否存在
        SysUser existingUser = sysUserMapper.findByUserName(request.getUsername());
        if (existingUser != null) {
            throw new RuntimeException(ResultCode.USER_ALREADY_EXISTS.getMessage());
        }

        // 创建用户（不设置 id，由数据库自增生成）
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickName(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(1); // 正常状态
        user.setDeleted(1); // 未删除
        user.setSex(1); // 默认男
        user.setCreateWhere(1); // web 注册
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 使用 insertSelective 插入用户，不传入 id 字段，让数据库自增
        // MyBatis 的 useGeneratedKeys=true 会自动将生成的主键回填到 user.id
        sysUserMapper.insertSelective(user);
        log.info("用户注册成功：id={}, username={}", user.getId(), user.getUsername());

        // 插入后重新从数据库查询完整的用户信息（确保数据一致性）
        SysUser savedUser = sysUserMapper.selectByPrimaryKey(user.getId());
        return convertToVO(savedUser != null ? savedUser : user);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        log.info("刷新 Token");

        // 验证 Refresh Token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new RuntimeException(ResultCode.TOKEN_INVALID.getMessage());
        }

        // 检查是否过期
        if (jwtTokenProvider.isTokenExpired(refreshToken)) {
            throw new RuntimeException(ResultCode.TOKEN_EXPIRED.getMessage());
        }

        // 获取用户名
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // 查询用户
        SysUser user = sysUserMapper.findByUserName(username);
        if (user == null || user.getStatus() != 1) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        // 生成新的 Token
        String authorities = getUserAuthorities(user.getId());
        String newAccessToken = jwtTokenProvider.generateToken(username, authorities);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(convertToVO(user))
                .build();
    }

    @Override
    public UserVO getUserInfo(String username) {
        SysUser user = sysUserMapper.findByUserName(username);
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }
        return convertToVO(user);
    }

    @Override
    public SysUser getUserByUsername(String username) {
        return sysUserMapper.findByUserName(username);
    }

    @Override
    public SysUser getUserById(Long id) {
        return sysUserMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUserProfile(String username, com.me.stock.user.dto.request.UpdateProfileRequest request) {
        SysUser user = sysUserMapper.findByUserName(username);
        if (user == null) {
            throw new RuntimeException(ResultCode.USER_NOT_FOUND.getMessage());
        }

        // 修改允许的字段
        if (request.getNickname() != null) {
            user.setNickName(request.getNickname());
        }
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        user.setUpdateTime(new Date());

        // 更新数据库
        sysUserMapper.updateByPrimaryKeySelective(user);
        log.info("用户更新信息成功: username={}", username);

        // 返回最新的完整的 UserVO
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(SysUser user) {
        return sysUserMapper.updateByPrimaryKeySelective(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(Long userId) {
        // 由于数据库没有 last_login_time 字段，这里不实现
        log.warn("updateLastLoginTime 不被支持，数据库无此字段");
    }

    /**
     * 获取用户的权限字符串
     */
    private String getUserAuthorities(Long userId) {
        // 获取用户的角色
        java.util.List<SysRole> roles = sysRoleMapper.getRoleByUserId(userId);
        if (roles.isEmpty()) {
            return "ROLE_USER";
        }
        // 返回第一个角色的名称
        return "ROLE_" + roles.get(0).getName();
    }

    /**
     * 转换为用户 VO
     */
    private UserVO convertToVO(SysUser user) {
        // 获取用户角色（仅当 id 不为 null 时才查询）
        String roleName = "USER";
        if (user.getId() != null) {
            java.util.List<SysRole> roles = sysRoleMapper.getRoleByUserId(user.getId());
            if (!roles.isEmpty()) {
                roleName = roles.get(0).getName();
            }
        }

        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .realName(user.getRealName())
                .roleName(roleName)
                .status(user.getStatus())
                .createTime(user.getCreateTime())
                .build();
    }
}
