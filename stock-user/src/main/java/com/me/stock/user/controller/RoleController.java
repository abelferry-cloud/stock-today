package com.me.stock.user.controller;

import com.me.stock.pojo.entity.SysRole;
import com.me.stock.user.common.Result;
import com.me.stock.user.service.RoleService;
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
 * 角色控制器
 *
 * @author stock-user
 */
@Slf4j
@Tag(name = "角色管理", description = "角色查询、创建、更新、删除等接口")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "获取角色列表", description = "获取所有角色")
    @GetMapping
    public Result<List<SysRole>> getRoles() {
        List<SysRole> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    @Operation(summary = "获取角色详情", description = "根据 ID 获取角色详情")
    @GetMapping("/{id}")
    public Result<SysRole> getRoleById(
            @Parameter(description = "角色 ID") @PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    @Operation(summary = "创建角色", description = "创建新角色（需要管理员权限）")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Result<Void> createRole(@RequestBody SysRole role) {
        boolean success = roleService.createRole(role);
        return success ? Result.success() : Result.error();
    }

    @Operation(summary = "更新角色", description = "更新角色信息（需要管理员权限）")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Result<Void> updateRole(
            @Parameter(description = "角色 ID") @PathVariable Long id,
            @RequestBody SysRole role) {
        role.setId(id);
        boolean success = roleService.updateRole(role);
        return success ? Result.success() : Result.error();
    }

    @Operation(summary = "删除角色", description = "删除指定角色（需要管理员权限）")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(
            @Parameter(description = "角色 ID") @PathVariable Long id) {
        boolean success = roleService.deleteRole(id);
        return success ? Result.success() : Result.error();
    }

    @Operation(summary = "为用户分配角色", description = "为用户分配指定角色（需要管理员权限）")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/assign")
    public Result<Void> assignRole(
            @Parameter(description = "用户 ID") @RequestParam Long userId,
            @Parameter(description = "角色 ID") @RequestParam Long roleId) {
        boolean success = roleService.assignRoleToUser(userId, roleId);
        return success ? Result.success() : Result.error();
    }
}
