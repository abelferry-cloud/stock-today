package com.me.stock.user.security;

import com.me.stock.mapper.SysPermissionMapper;
import com.me.stock.mapper.SysRoleMapper;
import com.me.stock.mapper.SysUserMapper;
import com.me.stock.pojo.entity.SysPermission;
import com.me.stock.pojo.entity.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户详细信息服务
 * 加载用户详细信息用于 Spring Security 认证
 *
 * @author stock-user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysPermissionMapper sysPermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户详细信息：username={}", username);

        // 查询用户
        SysUser user = sysUserMapper.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        // 检查用户是否被禁用
        if (user.getStatus() != null && user.getStatus() != 1) {
            throw new UsernameNotFoundException("用户已被禁用：" + username);
        }

        // 构建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 添加角色权限
        List<com.me.stock.pojo.entity.SysRole> roles = sysRoleMapper.getRoleByUserId(user.getId());
        for (com.me.stock.pojo.entity.SysRole role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }

        // 获取用户的权限标识
        List<String> perms = sysUserMapper.getUserPermissions(user.getId());
        for (String perm : perms) {
            if (perm != null && !perm.trim().isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(perm.trim()));
            }
        }

        // 如果没有任何权限，赋予默认角色
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities
        );
    }
}
