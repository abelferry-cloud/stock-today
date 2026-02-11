package com.me.stock.pojo.entity;

import java.util.Date;
import lombok.Data;

/**
 * 系统日志
 * @TableName sys_log
 */
@Data
public class SysLog {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户操作：DELETE ADD GET UPDATE
     */
    private String operation;

    /**
     * 响应时间,单位毫秒
     */
    private Integer time;

    /**
     * 请求方法（控制层方法全限定名）
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 创建时间
     */
    private Date createTime;
}