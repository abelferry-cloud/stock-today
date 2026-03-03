package com.me.stock.user.service;

import com.me.stock.user.entity.SysLoginLog;

import java.util.List;

/**
 * 登录日志服务接口
 *
 * @author Jovan
 * @since 1.0.0
 */
public interface LoginLogService {

    /**
     * 记录登录日志
     *
     * @param userId 用户 ID
     * @param username 用户名
     * @param status 登录状态
     * @param msg 提示信息
     */
    void recordLoginLog(Long userId, String username, Integer status, String msg);

    /**
     * 查询用户登录日志
     *
     * @param userId 用户 ID
     * @return 登录日志列表
     */
    List<SysLoginLog> getUserLoginLogs(Long userId);

    /**
     * 分页查询登录日志
     *
     * @param username 用户名
     * @param status 登录状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 页码
     * @param size 每页数量
     * @return 登录日志列表
     */
    List<SysLoginLog> getLoginLogsByPage(String username, Integer status, String startTime,
                                         String endTime, int page, int size);

    /**
     * 查询总记录数
     *
     * @param username 用户名
     * @param status 登录状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总记录数
     */
    int getTotalCount(String username, Integer status, String startTime, String endTime);

    /**
     * 清理指定日期之前的日志
     *
     * @param days 天数
     * @return 删除结果
     */
    int cleanOldLogs(int days);
}
