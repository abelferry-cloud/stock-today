package com.me.stock.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Token 请求 DTO
 *
 * @author stock-user
 */
@Data
public class TokenRefreshRequest {

    /**
     * Refresh Token
     */
    @NotBlank(message = "Refresh Token 不能为空")
    private String refreshToken;
}
