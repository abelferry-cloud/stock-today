package com.me.stock.user.controller;

import com.me.stock.pojo.entity.SysPermission;
import com.me.stock.pojo.vo.MenuNode;
import com.me.stock.user.common.Result;
import com.me.stock.user.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@Tag(name = "PermissionController", description = "权限管理接口")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 获取所有权限列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有权限", description = "获取所有可用的权限列表")
    @PreAuthorize("hasAnyAuthority('sys:permission:query', 'admin')")
    public Result<List<SysPermission>> listPermissions() {
        List<SysPermission> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }

    /**
     * 根据 ID 查询权限
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询权限", description = "根据权限 ID 查询权限详细信息")
    @PreAuthorize("hasAnyAuthority('sys:permission:query', 'admin')")
    public Result<SysPermission> getPermissionById(@PathVariable Long id) {
        SysPermission permission = permissionService.getPermissionById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    /**
     * 添加权限
     */
    @PostMapping
    @Operation(summary = "添加权限", description = "创建新的权限")
    @PreAuthorize("hasAnyAuthority('sys:permission:add', 'admin')")
    public Result<Void> addPermission(@RequestBody SysPermission permission) {
        permissionService.addPermission(permission);
        return Result.success(null);
    }

    /**
     * 更新权限
     */
    @PutMapping
    @Operation(summary = "更新权限", description = "更新权限信息")
    @PreAuthorize("hasAnyAuthority('sys:permission:edit', 'admin')")
    public Result<Void> updatePermission(@RequestBody SysPermission permission) {
        permissionService.updatePermission(permission);
        return Result.success(null);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "根据权限 ID 删除权限")
    @PreAuthorize("hasAnyAuthority('sys:permission:delete', 'admin')")
    public Result<Void> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success(null);
    }

    /**
     * 获取用户的菜单权限树
     */
    @GetMapping("/menus")
    @Operation(summary = "获取用户菜单权限", description = "获取当前用户的菜单权限树形结构")
    public Result<List<MenuNode>> getUserMenus(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Result.error("用户未登录");
        }
        // 需要从用户信息中获取 userId，这里简化处理
        List<MenuNode> menus = permissionService.getUserMenus(1L); // TODO: 从当前用户获取 userId
        return Result.success(menus);
    }

    /**
     * 获取用户的按钮权限列表
     */
    @GetMapping("/buttons")
    @Operation(summary = "获取用户按钮权限", description = "获取当前用户的按钮权限标识列表")
    public Result<List<String>> getUserPermissions(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Result.error("用户未登录");
        }
        List<String> permissions = permissionService.getUserPermissions(1L); // TODO: 从当前用户获取 userId
        return Result.success(permissions);
    }

    /**
     * 获取角色的权限列表
     */
    @GetMapping("/role/{roleId}")
    @Operation(summary = "获取角色权限", description = "获取指定角色的权限列表")
    @PreAuthorize("hasAnyAuthority('sys:permission:query', 'admin')")
    public Result<List<SysPermission>> getRolePermissions(@PathVariable Long roleId) {
        List<SysPermission> permissions = permissionService.getRolePermissions(roleId);
        return Result.success(permissions);
    }

    /**
     * 为角色分配权限
     */
    @PostMapping("/assign")
    @Operation(summary = "为角色分配权限", description = "为角色分配一个或多个权限")
    @PreAuthorize("hasAnyAuthority('sys:permission:assign', 'admin')")
    public Result<Void> assignPermissions(@RequestParam Long roleId, @RequestBody List<Long> permissionIds) {
        permissionService.assignPermissions(roleId, permissionIds);
        return Result.success(null);
    }
}
