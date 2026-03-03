package com.me.stock.user.service.impl;

import com.me.stock.mapper.SysPermissionMapper;
import com.me.stock.mapper.SysRolePermissionMapper;
import com.me.stock.pojo.entity.SysPermission;
import com.me.stock.pojo.entity.SysRolePermission;
import com.me.stock.pojo.vo.MenuNode;
import com.me.stock.user.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 获取所有权限
     */
    @Override
    public List<SysPermission> getAllPermissions() {
        return sysPermissionMapper.selectAll();
    }

    /**
     * 根据 ID 查询权限
     */
    @Override
    public SysPermission getPermissionById(Long id) {
        return sysPermissionMapper.selectByPrimaryKey(id);
    }

    /**
     * 添加权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPermission(SysPermission permission) {
        permission.setStatus(1);
        permission.setDeleted(1);
        permission.setCreateTime(new Date());
        permission.setUpdateTime(new Date());
        sysPermissionMapper.addPerms(permission);
        log.info("添加权限成功：{}", permission.getTitle());
    }

    /**
     * 更新权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePermission(SysPermission permission) {
        permission.setUpdateTime(new Date());
        sysPermissionMapper.updatePerms(permission);
        log.info("更新权限成功：{}", permission.getId());
    }

    /**
     * 删除权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePermission(Long id) {
        sysPermissionMapper.deleteByPrimaryKey(id);
        log.info("删除权限成功：{}", id);
    }

    /**
     * 获取用户的菜单权限树
     */
    @Override
    public List<MenuNode> getUserMenus(Long userId) {
        // 调用 SysUserMapper 的方法
        return null; // TODO: 需要在 SysUserMapper 中添加 getUserMenus 方法
    }

    /**
     * 获取用户的按钮权限列表
     */
    @Override
    public List<String> getUserPermissions(Long userId) {
        // 调用 SysUserMapper 的方法
        return null; // TODO: 需要在 SysUserMapper 中添加 getUserPermissions 方法
    }

    /**
     * 获取角色的权限列表
     */
    @Override
    public List<SysPermission> getRolePermissions(Long roleId) {
        // 这里需要根据角色 ID 查询权限，可能需要新增 Mapper 方法
        // 简化实现：返回所有权限
        return sysPermissionMapper.selectAll();
    }

    /**
     * 为角色分配权限
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        // 先删除角色原有权限
        // 注意：实际实现需要先查询现有的角色 - 权限关系，然后删除
        // 这里简化处理

        // 分配新权限
        List<SysRolePermission> list = new ArrayList<>();
        for (Long permissionId : permissionIds) {
            SysRolePermission rolePermission = new SysRolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            list.add(rolePermission);
        }

        if (!list.isEmpty()) {
            sysPermissionMapper.insertPerms(list);
        }

        log.info("为角色 {} 分配权限：{}", roleId, permissionIds);
    }
}
