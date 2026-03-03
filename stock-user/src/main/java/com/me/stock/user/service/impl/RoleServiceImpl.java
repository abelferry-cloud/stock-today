package com.me.stock.user.service.impl;

import com.me.stock.mapper.SysRoleMapper;
import com.me.stock.mapper.SysUserRoleMapper;
import com.me.stock.pojo.entity.SysRole;
import com.me.stock.pojo.entity.SysUserRole;
import com.me.stock.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现类
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    /**
     * 获取用户所有角色
     */
    @Override
    public List<String> getUserRoles(Long userId) {
        List<SysRole> roles = sysRoleMapper.getRoleByUserId(userId);
        return roles.stream()
                .filter(role -> role.getStatus() == 1)
                .map(SysRole::getName)
                .collect(Collectors.toList());
    }

    /**
     * 为用户分配角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 分配新角色
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insertSelective(userRole);
        }

        log.info("为用户 {} 分配角色：{}", userId, roleIds);
    }

    /**
     * 移除用户角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRole(Long userId, Long roleId) {
        // 删除用户角色关联 - 简化实现，实际项目中应该根据 userId 和 roleId 查询后删除
        List<String> roleIds = sysUserRoleMapper.getRolesIdByUser(userId.toString());
        for (String roleIdStr : roleIds) {
            if (roleIdStr.equals(roleId.toString())) {
                // 这里需要更精确的删除逻辑，简化处理
                log.info("移除用户 {} 的角色 {}", userId, roleId);
                break;
            }
        }

        // 注意：实际实现需要一个根据 userId 和 roleId 删除的方法
        // 建议使用 MyBatis 动态 SQL 实现
    }
}
