package com.me.stock.user.controller;

import com.me.stock.user.common.Result;
import com.me.stock.user.dto.response.UserVO;
import com.me.stock.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author stock-user
 */
@Slf4j
@Tag(name = "用户管理", description = "用户信息查询、更新等接口")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        // 用户名从 Security 上下文获取
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        UserVO userVO = userService.getUserInfo(username);
        return Result.success(userVO);
    }

    @Operation(summary = "更新当前用户信息", description = "更新当前登录用户的部分信息(昵称,真实姓名,手机号,邮箱)")
    @PutMapping("/me")
    public Result<UserVO> updateCurrentUser(
            @jakarta.validation.Valid @RequestBody com.me.stock.user.dto.request.UpdateProfileRequest request) {
        // 用户名从 Security 上下文获取
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        UserVO userVO = userService.updateUserProfile(username, request);
        return Result.success("更新个人信息成功", userVO);
    }

    @Operation(summary = "获取用户详情", description = "根据 ID 获取用户详情")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户 ID") @PathVariable Long id) {
        UserVO userVO = userService.getUserInfo(userService.getUserById(id).getUsername());
        if (userVO == null) {
            return Result.error("用户不存在");
        }
        return Result.success(userVO);
    }
}
