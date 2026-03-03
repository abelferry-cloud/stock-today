package com.me.stock.user.service;

import com.me.stock.pojo.entity.SysUser;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author Jovan
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 获取用户所有角色
     *
     * @param userId 用户 ID
     * @return 角色列表
     */
    List<String> getUserRoles(Long userId);

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
}
