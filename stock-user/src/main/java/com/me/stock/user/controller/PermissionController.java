package com.me.stock.user.controller;

import com.me.stock.pojo.entity.SysPermission;
import com.me.stock.user.common.Result;
import com.me.stock.user.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 *
 * @author stock-user
 */
@Slf4j
@Tag(name = "权限管理", description = "权限查询、创建、更新、删除等接口")
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "获取权限列表", description = "获取所有权限")
    @GetMapping
    public Result<List<SysPermission>> getPermissions() {
        List<SysPermission> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }

    @Operation(summary = "获取权限详情", description = "根据 ID 获取权限详情")
    @GetMapping("/{id}")
    public Result<SysPermission> getPermissionById(
            @Parameter(description = "权限 ID") @PathVariable Long id) {
        SysPermission permission = permissionService.getPermissionById(id);
        if (permission == null) {
            return Result.error("权限不存在");
        }
        return Result.success(permission);
    }

    @Operation(summary = "创建权限", description = "创建新权限（需要管理员权限）")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result<Void> createPermission(@RequestBody SysPermission permission) {
        boolean success = permissionService.createPermission(permission);
        return success ? Result.success() : Result.error();
    }

    @Operation(summary = "更新权限", description = "更新权限信息（需要管理员权限）")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Result<Void> updatePermission(
            @Parameter(description = "权限 ID") @PathVariable Long id,
            @RequestBody SysPermission permission) {
        permission.setId(id);
        boolean success = permissionService.updatePermission(permission);
        return success ? Result.success() : Result.error();
    }

    @Operation(summary = "删除权限", description = "删除指定权限（需要管理员权限）")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> deletePermission(
            @Parameter(description = "权限 ID") @PathVariable Long id) {
        boolean success = permissionService.deletePermission(id);
        return success ? Result.success() : Result.error();
    }

    @Operation(summary = "根据角色 ID 获取权限列表", description = "获取指定角色的权限列表")
    @GetMapping("/role/{roleId}")
    public Result<List<SysPermission>> getPermissionsByRoleId(
            @Parameter(description = "角色 ID") @PathVariable Long roleId) {
        List<SysPermission> permissions = permissionService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }
}
