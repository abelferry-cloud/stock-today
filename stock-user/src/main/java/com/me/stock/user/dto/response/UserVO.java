package com.me.stock.user.dto.response;

import com.me.stock.pojo.domain.SysUserDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 用户信息响应 DTO
 *
 * @author Jovan
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息响应")
public class UserVO {

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

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "性别（1 男 2 女）", example = "1")
    private Integer sex;

    @Schema(description = "账户状态（1 正常 2 锁定）", example = "1")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "角色列表", example = "[\"ADMIN\", \"USER\"]")
    private List<String> roles;

    /**
     * 从 SysUserDomain 实体转换
     */
    public static UserVO fromEntity(SysUserDomain entity) {
        return UserVO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .nickName(entity.getNickName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .realName(entity.getRealName())
                .sex(entity.getSex())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }
}
