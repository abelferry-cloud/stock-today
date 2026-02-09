package com.me.stock.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 菜单节点
 */
// @Schema(description = "菜单节点")
@Data
public class MenuNode {
    /**
     * 权限ID
     */
    // @Schema(value = "权限ID")
    private String id;

    /**
     * 父级权限ID
     */
    // @Schema(value = "父级权限ID")
    private String pid;

    /**
     * 权限标题
     */
    // @Schema(value = "权限标题")
    private String title;

    /**
     * 菜单图标
     */
    // @Schema(value = "权限图标")
    private String icon;

    /**
     * 请求地址
     */
    // @Schema(value = "请求地址")
    private String path;

    /**
     * 权限名称对应前端vue组件名称
     */
    // @Schema(value = "权限名称对应前端vue组件名称")
    private String name;

    /**
     * 子菜单
     */
    // @Schema(value = "子菜单")
    private List<MenuNode> children;
}