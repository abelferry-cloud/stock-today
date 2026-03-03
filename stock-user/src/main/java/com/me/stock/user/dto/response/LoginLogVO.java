package com.me.stock.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 登录日志响应 DTO
 *
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录日志响应")
public class LoginLogVO {

    @Schema(description = "日志 ID", example = "1")
    private Long id;

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "登录状态（1 成功 0 失败）", example = "1")
    private Integer status;

    @Schema(description = "登录 IP 地址", example = "192.168.1.100")
    private String ip;

    @Schema(description = "登录地点", example = "北京市")
    private String location;

    @Schema(description = "浏览器类型", example = "Chrome 120.0")
    private String browser;

    @Schema(description = "操作系统", example = "Windows 10")
    private String os;

    @Schema(description = "提示信息", example = "登录成功")
    private String msg;

    @Schema(description = "登录时间")
    private Date loginTime;
}
