package com.me.stock.user.service;

import com.me.stock.pojo.entity.SysPermission;

import java.util.List;

/**
 * 权限服务接口
 *
 * @author stock-user
 */
public interface PermissionService {

    /**
     * 创建权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean createPermission(SysPermission permission);

    /**
     * 更新权限
     *
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean updatePermission(SysPermission permission);

    /**
     * 删除权限
     *
     * @param id 权限 ID
     * @return 是否成功
     */
    boolean deletePermission(Long id);

    /**
     * 根据 ID 获取权限
     *
     * @param id 权限 ID
     * @return 权限信息
     */
    SysPermission getPermissionById(Long id);

    /**
     * 根据名称获取权限
     *
     * @param name 权限名称
     * @return 权限信息
     */
    SysPermission getPermissionByName(String name);

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    List<SysPermission> getAllPermissions();

    /**
     * 根据角色 ID 获取权限列表
     *
     * @param roleId 角色 ID
     * @return 权限列表
     */
    List<SysPermission> getPermissionsByRoleId(Long roleId);
}
