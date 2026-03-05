package com.me.stock.user.service;

import com.me.stock.pojo.entity.SysRole;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author stock-user
 */
public interface RoleService {

    /**
     * 创建角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean createRole(SysRole role);

    /**
     * 更新角色
     *
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(SysRole role);

    /**
     * 删除角色
     *
     * @param id 角色 ID
     * @return 是否成功
     */
    boolean deleteRole(Long id);

    /**
     * 根据 ID 获取角色
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    SysRole getRoleById(Long id);

    /**
     * 根据名称获取角色
     *
     * @param name 角色名称
     * @return 角色信息
     */
    SysRole getRoleByName(String name);

    /**
     * 获取所有角色
     *
     * @return 角色列表
     */
    List<SysRole> getAllRoles();

    /**
     * 为用户分配角色
     *
     * @param userId 用户 ID
     * @param roleId 角色 ID
     * @return 是否成功
     */
    boolean assignRoleToUser(Long userId, Long roleId);

    /**
     * 获取用户的默认角色
     *
     * @return 默认角色
     */
    SysRole getDefaultRole();
}
