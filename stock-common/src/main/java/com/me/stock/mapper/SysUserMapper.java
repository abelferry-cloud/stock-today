package com.me.stock.mapper;

import com.me.stock.pojo.domain.SysUserDomain;
import com.me.stock.pojo.entity.SysUser;
import com.me.stock.pojo.vo.MenuNode;
import com.me.stock.pojo.vo.UserInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author Jovan
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.SysUser
*/
public interface SysUserMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser findByUserName(String username);

    /**
     * 根据条件查询用户信息
     * @param userName 用户名
     * @param nickName 昵称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户信息
     */
    List<SysUserDomain> getUsersInfoByMCondition(@Param("userName") String userName, @Param("nickName") String nickName, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    SysUser findUserInfoByUserName(@Param("username") String username);

    /**
     * 添加用户
     * @param user 用户信息
     * @return 添加结果
     */
    int addUser(SysUser user);

    /**
     * 获取用户所拥有的角色id
     * @param userId 用户id
     * @return 角色id列表
     */
    List<Long> getUserOwnRoleIds(@Param("userId") String userId);

    /**
     * 批量删除用户
     * @param userIds 用户id列表
     * @return 删除结果
     */
    int deleteUsers(@Param("userIds") List<Long> userIds);

    /**
     * 根据用户id查询用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    UserInfoVO getUserInfoById(@Param("userId") Long userId);

    /**
     * 修改用户信息
     * @param sysUser 用户信息
     * @return 修改结果
     */
    int updateUserInfo(SysUser sysUser);

    /**
     * 根据用户ID查询用户菜单权限（不包含按钮权限）
     * @param userId 用户ID
     * @return 菜单权限列表
     */
    List<MenuNode> getUserMenus(@Param("userId") Long userId);

    /**
     * 根据用户ID查询用户按钮权限
     * @param userId 用户ID
     * @return 按钮权限标识列表
     */
    List<String> getUserPermissions(@Param("userId") Long userId);
}
