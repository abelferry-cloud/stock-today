package com.me.stock.mapper;

import com.me.stock.pojo.entity.SysRole;
import com.me.stock.pojo.vo.RoleVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 周俊远
* @description 针对表【sys_role(角色表)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.SysRole
*/
public interface SysRoleMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    /**
     * 获取所有角色信息
     * @return 角色信息
     */
    List<RoleVO> getAllRole();

    List<SysRole> selectAll();

    int addRole(@Param("role") SysRole role);

    int deleteRoles(@Param("roleId") String roleId);

    int updateRoleStatus(@Param("roleId") String roleId, @Param("status") Integer status);

    List<SysRole> getRoleByUserId(@Param("id") Long id);
}
