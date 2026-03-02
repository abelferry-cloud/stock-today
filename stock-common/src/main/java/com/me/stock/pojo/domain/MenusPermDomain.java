package com.me.stock.pojo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 权限树结构
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "权限树结构")
public class MenusPermDomain {
    /**
     * 权限id
     */
    @Schema(description = "权限id")
    private Long id;

    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    private String title;

    /**
     * 权限图标（按钮权限无图片）
     */
    @Schema(description = "权限图标（按钮权限无图片）")
    private String icon;

    /**
     * 请求地址
     */
    @Schema(description = "请求地址")
    private String path;

    /**
     * 权限名称对应前端vue组件名称
     */
    @Schema(description = "权限名称对应前端vue组件名称")
    private String name;

    /**
     * 子权限
     */
    @Schema(description = "子权限")
    private List<MenusPermDomain> children;
}
