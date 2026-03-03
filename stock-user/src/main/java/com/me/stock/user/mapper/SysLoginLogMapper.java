package com.me.stock.user.mapper;

import com.me.stock.user.entity.SysLoginLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 登录日志 Mapper 接口
 *
 * @author Jovan
 * @since 1.0.0
 */
public interface SysLoginLogMapper {

    int insert(SysLoginLog record);

    int insertSelective(SysLoginLog record);

    SysLoginLog selectByPrimaryKey(Long id);

    /**
     * 根据用户 ID 查询登录日志
     *
     * @param userId 用户 ID
     * @return 登录日志列表
     */
    List<SysLoginLog> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户名查询登录日志
     *
     * @param username 用户名
     * @return 登录日志列表
     */
    List<SysLoginLog> selectByUsername(@Param("username") String username);

    /**
     * 分页查询登录日志
     *
     * @param username 用户名
     * @param status 登录状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 登录日志列表
     */
    List<SysLoginLog> selectByCondition(@Param("username") String username,
                                        @Param("status") Integer status,
                                        @Param("startTime") Date startTime,
                                        @Param("endTime") Date endTime,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    /**
     * 查询总记录数
     *
     * @param username 用户名
     * @param status 登录状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总记录数
     */
    int countByCondition(@Param("username") String username,
                         @Param("status") Integer status,
                         @Param("startTime") Date startTime,
                         @Param("endTime") Date endTime);

    /**
     * 删除登录日志
     *
     * @param id 日志 ID
     * @return 删除结果
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 批量删除登录日志
     *
     * @param ids 日志 ID 列表
     * @return 删除结果
     */
    int deleteByIds(@Param("ids") List<Long> ids);

    /**
     * 清理指定日期之前的日志
     *
     * @param beforeDate 日期
     * @return 删除结果
     */
    int deleteBeforeDate(@Param("beforeDate") Date beforeDate);
}
