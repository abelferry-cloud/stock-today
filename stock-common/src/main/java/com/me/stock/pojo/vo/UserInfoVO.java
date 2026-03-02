package com.me.stock.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户信息")
public class UserInfoVO {
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long id;

    /**
     * 账户名
     */
    @Schema(description = "账户名")
    private String username;

    /**
     * 用户手机号
     */
    @Schema(description = "用户手机号")
    private String phone;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickName;

    /**
     * 用户真实姓名
     */
    @Schema(description = "用户真实姓名")
    private String realName;

    /**
     * 用户性别（1.男，2.女）
     */
    @Schema(description = "用户性别（1.男，2.女）")
    private Integer sex;

    /**
     * 用户状态（1.正常，2.锁定）
     */
    @Schema(description = "用户状态（1.正常，2.锁定）")
    private Integer status;

    /**
     * 用户邮箱（唯一）
     */
    @Schema(description = "用户邮箱（唯一）")
    private String email;




}
