package com.me.stock.user.controller;

import com.me.stock.pojo.entity.SysRole;
import com.me.stock.user.common.Result;
import com.me.stock.user.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "RoleController", description = "角色管理接口")
public class RoleController {

    private final RoleService roleService;

    /**
     * 获取所有角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取所有角色", description = "获取所有可用的角色列表")
    @PreAuthorize("hasAnyAuthority('sys:role:query', 'admin')")
    public Result<List<SysRole>> listRoles() {
        List<SysRole> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    /**
     * 根据 ID 查询角色
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询角色", description = "根据角色 ID 查询角色详细信息")
    @PreAuthorize("hasAnyAuthority('sys:role:query', 'admin')")
    public Result<SysRole> getRoleById(@PathVariable Long id) {
        SysRole role = roleService.getRoleById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 添加角色
     */
    @PostMapping
    @Operation(summary = "添加角色", description = "创建新的角色")
    @PreAuthorize("hasAnyAuthority('sys:role:add', 'admin')")
    public Result<Void> addRole(@RequestBody SysRole role) {
        roleService.addRole(role);
        return Result.success(null);
    }

    /**
     * 更新角色
     */
    @PutMapping
    @Operation(summary = "更新角色", description = "更新角色信息")
    @PreAuthorize("hasAnyAuthority('sys:role:edit', 'admin')")
    public Result<Void> updateRole(@RequestBody SysRole role) {
        roleService.updateRole(role);
        return Result.success(null);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "根据角色 ID 删除角色")
    @PreAuthorize("hasAnyAuthority('sys:role:delete', 'admin')")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success(null);
    }

    /**
     * 更新角色状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新角色状态", description = "启用或禁用角色")
    @PreAuthorize("hasAnyAuthority('sys:role:edit', 'admin')")
    public Result<Void> updateRoleStatus(@PathVariable Long id, @RequestParam Integer status) {
        roleService.updateRoleStatus(id, status);
        return Result.success(null);
    }

    /**
     * 为用户分配角色
     */
    @PostMapping("/assign")
    @Operation(summary = "为用户分配角色", description = "为用户分配一个或多个角色")
    @PreAuthorize("hasAnyAuthority('sys:role:assign', 'admin')")
    public Result<Void> assignRoles(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        roleService.assignRoles(userId, roleIds);
        return Result.success(null);
    }

    /**
     * 获取用户的角色列表
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的角色", description = "获取指定用户的角色列表")
    @PreAuthorize("hasAnyAuthority('sys:role:query', 'admin')")
    public Result<List<SysRole>> getUserRoles(@PathVariable Long userId) {
        List<SysRole> roles = roleService.getUserRolesEntity(userId);
        return Result.success(roles);
    }
}
