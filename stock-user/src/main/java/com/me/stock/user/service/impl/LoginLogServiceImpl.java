package com.me.stock.user.service.impl;

import com.me.stock.user.entity.SysLoginLog;
import com.me.stock.user.mapper.SysLoginLogMapper;
import com.me.stock.user.service.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 登录日志服务实现类
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final SysLoginLogMapper sysLoginLogMapper;

    /**
     * 记录登录日志（异步执行）
     */
    @Override
    @Async
    public void recordLoginLog(Long userId, String username, Integer status, String msg) {
        try {
            // 获取客户端信息（实际项目中需要从 RequestContext 获取）
            String ip = "127.0.0.1";
            String location = "本地";
            String browser = "Unknown";
            String os = "Unknown";

            SysLoginLog loginLog = SysLoginLog.builder()
                    .userId(userId)
                    .username(username)
                    .status(status)
                    .ip(ip)
                    .location(location)
                    .browser(browser)
                    .os(os)
                    .msg(msg)
                    .loginTime(new Date())
                    .createTime(new Date())
                    .build();

            sysLoginLogMapper.insertSelective(loginLog);
            log.info("记录登录日志：{}, 状态：{}", username, status == 1 ? "成功" : "失败");
        } catch (Exception e) {
            log.error("记录登录日志失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 查询用户登录日志
     */
    @Override
    public List<SysLoginLog> getUserLoginLogs(Long userId) {
        return sysLoginLogMapper.selectByUserId(userId);
    }

    /**
     * 分页查询登录日志
     */
    @Override
    public List<SysLoginLog> getLoginLogsByPage(String username, Integer status, String startTime,
                                                String endTime, int page, int size) {
        Date start = null;
        Date end = null;

        try {
            if (startTime != null && !startTime.isEmpty()) {
                start = new Date(Long.parseLong(startTime));
            }
            if (endTime != null && !endTime.isEmpty()) {
                end = new Date(Long.parseLong(endTime));
            }
        } catch (Exception e) {
            log.warn("时间格式转换失败：{}", e.getMessage());
        }

        int offset = (page - 1) * size;
        return sysLoginLogMapper.selectByCondition(username, status, start, end, offset, size);
    }

    /**
     * 查询总记录数
     */
    @Override
    public int getTotalCount(String username, Integer status, String startTime, String endTime) {
        Date start = null;
        Date end = null;

        try {
            if (startTime != null && !startTime.isEmpty()) {
                start = new Date(Long.parseLong(startTime));
            }
            if (endTime != null && !endTime.isEmpty()) {
                end = new Date(Long.parseLong(endTime));
            }
        } catch (Exception e) {
            log.warn("时间格式转换失败：{}", e.getMessage());
        }

        return sysLoginLogMapper.countByCondition(username, status, start, end);
    }

    /**
     * 清理指定日期之前的日志
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanOldLogs(int days) {
        Date beforeDate = new Date(System.currentTimeMillis() - days * 24L * 60 * 60 * 1000);
        int result = sysLoginLogMapper.deleteBeforeDate(beforeDate);
        log.info("清理{}天前的登录日志，删除{}条记录", days, result);
        return result;
    }
}
