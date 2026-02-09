package com.me.stock.mapper;

import com.me.stock.pojo.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 周俊远
* @description 针对表【sys_role_permission(角色权限表)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.SysRolePermission
*/
public interface SysRolePermissionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysRolePermission record);

    int insertSelective(SysRolePermission record);

    SysRolePermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRolePermission record);

    int updateByPrimaryKey(SysRolePermission record);

    List<Long> getPermsByRoleId(String roleId);

    int deleteByRoleId(Long id);

    void deleteByPermissionId(@Param("permissionId") Long permissionId);
}
