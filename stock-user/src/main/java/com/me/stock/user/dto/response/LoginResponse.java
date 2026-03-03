package com.me.stock.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应 DTO
 *
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginResponse {

    @Schema(description = "访问 Token")
    private String accessToken;

    @Schema(description = "刷新 Token")
    private String refreshToken;

    @Schema(description = "Token 类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "过期时间（毫秒）", example = "7200000")
    private Long expiresIn;

    @Schema(description = "用户信息")
    private UserInfoDTO userInfo;

    /**
     * 用户信息 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfoDTO {
        @Schema(description = "用户 ID", example = "1")
        private Long id;

        @Schema(description = "用户名", example = "admin")
        private String username;

        @Schema(description = "昵称", example = "管理员")
        private String nickName;

        @Schema(description = "手机号", example = "138****0000")
        private String phone;

        @Schema(description = "邮箱", example = "admin@example.com")
        private String email;

        @Schema(description = "头像 URL", example = "http://example.com/avatar.png")
        private String avatar;

        @Schema(description = "角色列表", example = "[\"ADMIN\", \"USER\"]")
        private List<String> roles;

        @Schema(description = "权限列表", example = "[\"sys:user:add\", \"sys:user:edit\"]")
        private List<String> permissions;
    }
}
