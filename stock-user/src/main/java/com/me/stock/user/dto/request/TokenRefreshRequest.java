package com.me.stock.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 刷新 Token 请求 DTO
 *
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Schema(description = "刷新 Token 请求")
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh Token 不能为空")
    @Schema(description = "刷新 Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}
