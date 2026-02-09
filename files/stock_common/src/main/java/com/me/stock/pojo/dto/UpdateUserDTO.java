package com.me.stock.pojo.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {
    /**
     * 用户i
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱（唯一）
     */
    private String email;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别(1.男 2.女)
     */
    private Integer sex;

    /**
     * 创建来源(1.web 2.android 3.ios )
     */
    private Integer createWhere;

    /**
     * 账户状态(1.正常 2.锁定 )
     */
    private Integer status;
}
