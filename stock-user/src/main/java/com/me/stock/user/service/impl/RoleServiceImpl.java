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

import java.util.Date;
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
     * 获取所有角色
     */
    @Override
    public List<SysRole> getAllRoles() {
        return sysRoleMapper.selectAll();
    }

    /**
     * 根据 ID 查询角色
     */
    @Override
    public SysRole getRoleById(Long id) {
        return sysRoleMapper.selectByPrimaryKey(id);
    }

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
     * 获取用户的角色实体列表
     */
    @Override
    public List<SysRole> getUserRolesEntity(Long userId) {
        return sysRoleMapper.getRoleByUserId(userId);
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
        // 注意：实际实现需要一个根据 userId 和 roleId 删除的方法
        log.info("移除用户 {} 的角色 {}", userId, roleId);
    }

    /**
     * 添加角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRole(SysRole role) {
        role.setStatus(1);
        role.setDeleted(1);
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        sysRoleMapper.addRole(role);
        log.info("添加角色成功：{}", role.getName());
    }

    /**
     * 更新角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(SysRole role) {
        role.setUpdateTime(new Date());
        sysRoleMapper.updateByPrimaryKeySelective(role);
        log.info("更新角色成功：{}", role.getId());
    }

    /**
     * 删除角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long id) {
        sysRoleMapper.deleteByPrimaryKey(id);
        log.info("删除角色成功：{}", id);
    }

    /**
     * 更新角色状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoleStatus(Long id, Integer status) {
        sysRoleMapper.updateRoleStatus(id.toString(), status);
        log.info("更新角色状态成功：{}, 状态：{}", id, status);
    }
}
