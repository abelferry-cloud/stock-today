package com.me.stock.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新个人信息请求 DTO
 * 
 * @author stock-user
 */
@Data
public class UpdateProfileRequest {

    /**
     * 昵称
     */
    @Size(max = 60, message = "昵称长度不能超过 60 个字符")
    private String nickname;

    /**
     * 真实姓名
     */
    @Size(max = 60, message = "真实姓名长度不能超过 60 个字符")
    private String realName;

    /**
     * 手机号
     */
    @Pattern(regexp = "^(1[3-9]\\d{9})?$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;
}
