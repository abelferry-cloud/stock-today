package com.me.stock.user.service;

import com.me.stock.mapper.SysPermissionMapper;
import com.me.stock.mapper.SysRoleMapper;
import com.me.stock.mapper.SysUserMapper;
import com.me.stock.pojo.entity.SysPermission;
import com.me.stock.pojo.entity.SysRole;
import com.me.stock.pojo.entity.SysUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义 UserDetailsService 实现类
 * 负责从数据库加载用户信息和权限
 *
 * @author Jovan
 * @since 1.0.0
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
        // 根据用户名查询用户信息
        SysUser user = sysUserMapper.findByUserName(username);
        if (user == null) {
            log.warn("用户不存在：{}", username);
            throw new UsernameNotFoundException("用户不存在：" + username);
        }

        // 检查用户状态
        if (user.getStatus() != null && user.getStatus() != 1) {
            log.warn("用户已被禁用：{}", username);
            throw new UsernameNotFoundException("用户已被禁用：" + username);
        }

        // 检查是否删除
        if (user.getDeleted() != null && user.getDeleted() == 0) {
            log.warn("用户已被删除：{}", username);
            throw new UsernameNotFoundException("用户已被删除：" + username);
        }

        // 加载用户角色
        List<SysRole> roles = sysRoleMapper.getRoleByUserId(user.getId());
        List<String> roleNames = roles.stream()
                .filter(role -> role.getStatus() == 1)
                .map(SysRole::getName)
                .collect(Collectors.toList());

        // 加载用户权限
        List<SysPermission> permissions = sysPermissionMapper.getPermsByUserId(user.getId());
        List<String> permissionCodes = permissions.stream()
                .filter(perm -> perm.getStatus() == 1)
                .map(SysPermission::getPerms)
                .collect(Collectors.toList());

        // 合并角色和权限为 authorities
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        roleNames.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        permissionCodes.forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm)));

        log.debug("加载用户信息成功：{}, 角色：{}, 权限：{}", username, roleNames, permissionCodes);

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
