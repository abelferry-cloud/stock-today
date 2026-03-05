package com.me.stock.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO
 *
 * @author stock-user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    /**
     * Access Token
     */
    private String accessToken;

    /**
     * Token 类型
     */
    @Builder.Default
    private String tokenType = "Bearer";

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * 过期时间（毫秒）
     */
    private Long expiresIn;

    /**
     * 用户信息
     */
    private UserVO user;
}
