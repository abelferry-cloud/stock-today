package com.me.stock.pojo.domain;

import io.swagger.annotations.ApiModelProperty;
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
public class SysUserDomain{

    //用户id
    @ApiModelProperty("用户id")
    private Long id;

    //账户名
    @ApiModelProperty("账户名")
    private String username;

    //用户密文密码
    @ApiModelProperty("用户密文密码")
    private String password;

    //用户手机号
    @ApiModelProperty("用户手机号")
    private String phone;

    //用户真实姓名
    @ApiModelProperty("用户真实姓名")
    private String realName;

    //用户昵称
    @ApiModelProperty("用户昵称")
    private String nickName;

    //用户邮箱（唯一）
    @ApiModelProperty("用户邮箱（唯一）")
    private String email;

    //用户状态（1.正常，2.锁定）
    @ApiModelProperty("用户状态（1.正常，2.锁定）")
    private Integer status;

    //用户性别（1.男，2.女）
    @ApiModelProperty("用户性别（1.男，2.女）")
    private Integer sex;

    //用户是否删除（1.未删，0.已删）
    @ApiModelProperty("用户是否删除（1.未删，0.已删）")
    private Integer deleted;

    //创建人id
    @ApiModelProperty("创建人id")
    private Long createId;

    //更新人id
    @ApiModelProperty("更新人id")
    private Long updateId;

    //创建来源（1.web，2.android,3.ios）
    @ApiModelProperty("创建来源（1.web，2.android,3.ios）")
    private Integer createWhere;

    //创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;

    //更新时间
    @ApiModelProperty("更新时间")
    private Date updateTime;

    //创建人姓名
    @ApiModelProperty("创建人姓名")
    private String createUserName;

    //更新人姓名
    @ApiModelProperty("更新人姓名")
    private String updateUserName;
}
