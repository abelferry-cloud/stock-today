package com.me.stock.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 添加用户请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiOperation(value = "添加用户请求参数")
public class AddUserReqVo {
    //账户名
    @ApiModelProperty("账户名")
    private String username;

    //用户密文密码
    @ApiModelProperty("用户密文密码")
    private String password;

    //用户手机号
    @ApiModelProperty("用户手机号")
    private String phone;

    //用户邮箱（唯一）
    @ApiModelProperty("用户邮箱（唯一）")
    private String email;

    //用户昵称
    @ApiModelProperty("用户昵称")
    private String nickName;

    //用户真实姓名
    @ApiModelProperty("用户真实姓名")
    private String realName;

    //用户性别（1.男，2.女）
    @ApiModelProperty("用户性别（1.男，2.女）")
    private Integer sex;

    //创建来源（1.web，2.android,3.ios）
    @ApiModelProperty("创建来源（1.web，2.android,3.ios）")
    private String createWhere;

    //用户状态（1.正常，2.锁定）
    @ApiModelProperty("用户状态（1.正常，2.锁定）")
    private Integer status;
}
