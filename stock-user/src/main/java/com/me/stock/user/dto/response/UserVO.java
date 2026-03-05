package com.me.stock.user.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 用户信息 VO
 *
 * @author stock-user
 */
@Data
@Builder
public class UserVO {

    /**
     * 用户 ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 状态（1-正常，2-锁定）
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
}
