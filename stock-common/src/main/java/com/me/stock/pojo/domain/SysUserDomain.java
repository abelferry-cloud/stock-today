package com.me.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 系统用户表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "系统用户表")
public class SysUserDomain{

    //用户id
    @Schema(description = "用户id")
    private Long id;

    //账户名
    @Schema(description = "账户名")
    private String username;

    //用户密文密码
    @Schema(description = "用户密文密码")
    private String password;

    //用户手机号
    @Schema(description = "用户手机号")
    private String phone;

    //用户真实姓名
    @Schema(description = "用户真实姓名")
    private String realName;

    //用户昵称
    @Schema(description = "用户昵称")
    private String nickName;

    //用户邮箱（唯一）
    @Schema(description = "用户邮箱（唯一）")
    private String email;

    //用户状态（1.正常，2.锁定）
    @Schema(description = "用户状态（1.正常，2.锁定）")
    private Integer status;

    //用户性别（1.男，2.女）
    @Schema(description = "用户性别（1.男，2.女）")
    private Integer sex;

    //用户是否删除（1.未删，0.已删）
    @Schema(description = "用户是否删除（1.未删，0.已删）")
    private Integer deleted;

    //创建人id
    @Schema(description = "创建人id")
    private Long createId;

    //更新人id
    @Schema(description = "更新人id")
    private Long updateId;

    //创建来源（1.web，2.android,3.ios）
    @Schema(description = "创建来源（1.web，2.android,3.ios）")
    private Integer createWhere;

    //创建时间
    @Schema(description = "创建时间")
    private Date createTime;

    //更新时间
    @Schema(description = "更新时间")
    private Date updateTime;

    //创建人姓名
    @Schema(description = "创建人姓名")
    private String createUserName;

    //更新人姓名
    @Schema(description = "更新人姓名")
    private String updateUserName;
}
