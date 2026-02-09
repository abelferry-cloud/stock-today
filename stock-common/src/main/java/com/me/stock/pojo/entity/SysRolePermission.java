package com.me.stock.pojo.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 角色权限表
 * @TableName sys_role_permission
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// @Api(tags = "角色权限表")
public class SysRolePermission {
    /**
     * 主键
     */
    private Long id;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 菜单权限id
     */
    private Long permissionId;

    /**
     * 创建时间
     */
    private Date createTime;
}