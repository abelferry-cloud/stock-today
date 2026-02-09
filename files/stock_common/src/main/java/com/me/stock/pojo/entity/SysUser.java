package com.me.stock.pojo.entity;

import java.io.Serializable;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户表
 * @TableName sys_user
 */
@ApiModel(description = "系统用户实体")
@Data
public class SysUser implements Serializable {
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户ID", example = "1")
    private Long id;

    /**
     * 账户
     */
    @ApiModelProperty(value = "用户账户", example = "admin")
    private String username;

    /**
     * 用户密码密文
     */
    @ApiModelProperty(value = "用户密码密文", hidden = true)
    private String password;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "13888888888")
    private String phone;

    /**
     * 真实名称
     */
    @ApiModelProperty(value = "真实姓名", example = "张三")
    private String realName;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "用户昵称", example = "管理员")
    private String nickName;

    /**
     * 邮箱(唯一)
     */
    @ApiModelProperty(value = "邮箱地址", example = "admin@example.com")
    private String email;

    /**
     * 账户状态(1.正常 2.锁定 )
     */
    @ApiModelProperty(value = "账户状态：1-正常，2-锁定", example = "1")
    private Integer status;

    /**
     * 性别(1.男 2.女)
     */
    @ApiModelProperty(value = "性别：1-男，2-女", example = "1")
    private Integer sex;

    /**
     * 是否删除(1未删除；0已删除)
     */
    @ApiModelProperty(value = "删除状态：1-未删除，0-已删除", example = "1")
    private Integer deleted;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人ID", example = "1")
    private Long createId;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人ID", example = "1")
    private Long updateId;

    /**
     * 创建来源(1.web 2.android 3.ios )
     */
    @ApiModelProperty(value = "创建来源：1-web，2-android，3-ios", example = "1")
    private Integer createWhere;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}