package com.me.stock.user.controller;

import com.me.stock.pojo.vo.UserMConditionReqVO;
import com.me.stock.user.common.Result;
import com.me.stock.user.dto.request.UserInfoRequest;
import com.me.stock.user.dto.response.LoginLogVO;
import com.me.stock.user.dto.response.UserVO;
import com.me.stock.user.entity.SysLoginLog;
import com.me.stock.pojo.domain.SysUserDomain;
import com.me.stock.user.service.LoginLogService;
import com.me.stock.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 *
 * @author Jovan
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "用户管理接口")
public class UserController {

    private final UserService userService;
    private final LoginLogService loginLogService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserVO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Result.error("用户未登录");
        }

        SysUserDomain user = userService.getUserInfoByUsername(userDetails.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }

        UserVO userVO = UserVO.fromEntity(user);
        return Result.success(userVO);
    }

    /**
     * 根据 ID 查询用户信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询用户", description = "根据用户 ID 查询用户详细信息")
    @PreAuthorize("hasAnyAuthority('sys:user:query', 'admin')")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        SysUserDomain user = userService.getUserInfoById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }

        UserVO userVO = UserVO.fromEntity(user);
        return Result.success(userVO);
    }

    /**
     * 条件查询用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "条件查询用户列表", description = "根据用户名、昵称、时间范围等条件查询用户列表")
    @PreAuthorize("hasAnyAuthority('sys:user:query', 'admin')")
    public Result<List<UserVO>> listUsers(UserMConditionReqVO reqVO) {
        List<SysUserDomain> users = userService.listUsersByCondition(reqVO);
        List<UserVO> result = users.stream()
                .map(UserVO::fromEntity)
                .collect(Collectors.toList());
        return Result.success(result);
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的基本信息")
    public Result<Void> updateUserInfo(@RequestBody UserInfoRequest request,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Result.error("用户未登录");
        }

        SysUserDomain currentUser = userService.getUserInfoByUsername(userDetails.getUsername());
        if (currentUser == null) {
            return Result.error("用户不存在");
        }

        userService.updateUserInfo(currentUser.getId(), request);
        return Result.success(null);
    }

    /**
     * 查询用户登录日志
     */
    @GetMapping("/login-logs")
    @Operation(summary = "查询用户登录日志", description = "查询当前用户的登录历史记录")
    public Result<List<LoginLogVO>> getUserLoginLogs(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Result.error("用户未登录");
        }

        SysUserDomain user = userService.getUserInfoByUsername(userDetails.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }

        List<SysLoginLog> logs = loginLogService.getUserLoginLogs(user.getId());
        List<LoginLogVO> result = logs.stream()
                .map(log -> LoginLogVO.builder()
                        .id(log.getId())
                        .userId(log.getUserId())
                        .username(log.getUsername())
                        .status(log.getStatus())
                        .ip(log.getIp())
                        .location(log.getLocation())
                        .browser(log.getBrowser())
                        .os(log.getOs())
                        .msg(log.getMsg())
                        .loginTime(log.getLoginTime())
                        .build())
                .collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码")
    public Result<Void> changePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return Result.error("用户未登录");
        }

        userService.changePassword(userDetails.getUsername(), oldPassword, newPassword);
        return Result.success(null);
    }
}
