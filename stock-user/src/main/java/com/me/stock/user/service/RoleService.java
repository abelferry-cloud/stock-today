package com.me.stock.user.service;

import com.me.stock.pojo.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author Jovan
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    List<SysRole> getAllRoles();

    /**
     * 根据 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    SysRole getRoleById(Long id);

    /**
     * 获取用户所有角色
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户的角色实体列表
     *
     * @param userId 用户 ID
     * @return 角色实体列表
     */
    List<SysRole> getUserRolesEntity(Long userId);

    /**
     * 为用户分配角色
     *
     * @param userId 用户 ID
     * @param roleIds 角色 ID 列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 移除用户角色
     *
     * @param userId 用户 ID
     * @param roleId 角色 ID
     */
    void removeUserRole(Long userId, Long roleId);

    /**
     * 添加角色
     *
     * @param role 角色信息
     */
    void addRole(SysRole role);

    /**
     * 更新角色
     *
     * @param role 角色信息
     */
    void updateRole(SysRole role);

    /**
     * 删除角色
     *
     * @param id 角色 ID
     */
    void deleteRole(Long id);

    /**
     * 更新角色状态
     *
     * @param id 角色 ID
     * @param status 状态
     */
    void updateRoleStatus(Long id, Integer status);
}
