package com.me.stock.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户信息请求 DTO
 *
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Schema(description = "用户信息请求")
public class UserInfoRequest {

    @Schema(description = "用户 ID", example = "1")
    private Long id;

    @Schema(description = "昵称", example = "管理员")
    private String nickName;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "性别（1 男 2 女）", example = "1")
    private Integer sex;
}
