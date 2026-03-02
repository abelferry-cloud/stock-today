package com.me.stock.mapper;

import com.me.stock.pojo.entity.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Jovan
* @description 针对表【sys_user_role(用户角色表)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.SysUserRole
*/
public interface SysUserRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

    /**
     * 根据用户id获取角色id
     * @param s
     * @return
     */
    List<String> getRolesIdByUser(String s);

    /**
     * 批量添加用户角色
     * @param userRoles
     * @return
     */
    int addUserRole(@Param("userRoles") List<SysUserRole> userRoles);
}
