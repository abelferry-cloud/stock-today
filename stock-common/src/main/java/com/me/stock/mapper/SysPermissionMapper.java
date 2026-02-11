package com.me.stock.mapper;

import com.me.stock.pojo.entity.SysPermission;
import com.me.stock.pojo.entity.SysRolePermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 周俊远
* @description 针对表【sys_permission(权限表（菜单）)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.SysPermission
*/
public interface SysPermissionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysPermission record);

    int insertSelective(SysPermission record);

    SysPermission selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysPermission record);

    int updateByPrimaryKey(SysPermission record);

    List<SysPermission> selectAll();

    int insertPerms(@Param("perms") List<SysRolePermission> list);

    int addPerms(@Param("addPerms") SysPermission addPerms);

    int updatePerms(@Param("addPerms") SysPermission addPerms);

    int findChildrenCountByParentId(@Param("permissionId") Long permissionId);

    List<SysPermission> getPermsByUserId(@Param("id") Long id);

}
