package com.me.stock.user.service;

import com.me.stock.pojo.entity.SysPermission;
import com.me.stock.pojo.vo.MenuNode;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author Jovan
 * @since 1.0.0
 */
public interface PermissionService {

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    List<SysPermission> getAllPermissions();

    /**
     * 根据 ID 查询权限
     *
     * @param id 权限 ID
     * @return 权限信息
     */
    SysPermission getPermissionById(Long id);

    /**
     * 添加权限
     *
     * @param permission 权限信息
     */
    void addPermission(SysPermission permission);

    /**
     * 更新权限
     *
     * @param permission 权限信息
     */
    void updatePermission(SysPermission permission);

    /**
     * 删除权限
     *
     * @param id 权限 ID
     */
    void deletePermission(Long id);

    /**
     * 获取用户的菜单权限树
     *
     * @param userId 用户 ID
     * @return 菜单权限树
     */
    List<MenuNode> getUserMenus(Long userId);

    /**
     * 获取用户的按钮权限列表
     *
     * @param userId 用户 ID
     * @return 按钮权限标识列表
     */
    List<String> getUserPermissions(Long userId);

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色 ID
     * @return 权限列表
     */
    List<SysPermission> getRolePermissions(Long roleId);

    /**
     * 为角色分配权限
     *
     * @param roleId 角色 ID
     * @param permissionIds 权限 ID 列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
