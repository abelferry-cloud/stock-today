-- ============================================
-- 智能股票分析系统 - 初始化 SQL 脚本
-- 功能：初始化默认管理员用户、基础角色和权限数据
-- 版本：1.0.0
-- 日期：2026-03-03
-- ============================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 初始化默认管理员用户
-- 用户名：admin
-- 密码：admin123 (BCrypt 加密后：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iH6sFHMWkqQaO7bMKpDvF1Rn.7G.)
-- ============================================

INSERT INTO `sys_user` (`id`, `username`, `password`, `phone`, `real_name`, `nick_name`, `email`, `status`, `sex`, `deleted`, `create_id`, `update_id`, `create_where`, `create_time`, `update_time`)
VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iH6sFHMWkqQaO7bMKpDvF1Rn.7G.', '13800138000', '系统管理员', '管理员', 'admin@example.com', 1, 1, 1, NULL, NULL, 1, NOW(), NOW()),
(2, 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iH6sFHMWkqQaO7bMKpDvF1Rn.7G.', '13900139000', '普通用户', '用户', 'user@example.com', 1, 1, 1, NULL, NULL, 1, NOW(), NOW());

-- ============================================
-- 2. 初始化角色数据
-- ============================================

INSERT INTO `sys_role` (`id`, `name`, `description`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
(1, 'SUPER_ADMIN', '超级管理员', 1, NOW(), NOW(), 1),
(2, 'ADMIN', '管理员', 1, NOW(), NOW(), 1),
(3, 'USER', '普通用户', 1, NOW(), NOW(), 1);

-- ============================================
-- 3. 初始化权限数据（菜单 + 按钮）
-- ============================================

-- 一级菜单：系统管理
INSERT INTO `sys_permission` (`id`, `code`, `title`, `icon`, `perms`, `url`, `method`, `name`, `pid`, `order_num`, `type`, `status`, `create_time`, `update_time`, `deleted`)
VALUES
(1, 'system', '系统管理', 'setting', 'system', '/system', 'GET', 'System', 0, 1, 1, 1, NOW(), NOW(), 1),
-- 二级菜单：用户管理
(2, 'user', '用户管理', 'user', 'sys:user', '/system/user', 'GET', 'User', 1, 1, 1, 1, NOW(), NOW(), 1),
-- 用户管理按钮权限
(3, 'user:add', '新增用户', '', 'sys:user:add', '/system/user', 'POST', 'UserAdd', 2, 1, 3, 1, NOW(), NOW(), 1),
(4, 'user:edit', '编辑用户', '', 'sys:user:edit', '/system/user', 'PUT', 'UserEdit', 2, 2, 3, 1, NOW(), NOW(), 1),
(5, 'user:delete', '删除用户', '', 'sys:user:delete', '/system/user', 'DELETE', 'UserDelete', 2, 3, 3, 1, NOW(), NOW(), 1),
(6, 'user:query', '查询用户', '', 'sys:user:query', '/system/user', 'GET', 'UserQuery', 2, 4, 3, 1, NOW(), NOW(), 1),
-- 二级菜单：角色管理
(7, 'role', '角色管理', 'peoples', 'sys:role', '/system/role', 'GET', 'Role', 1, 2, 1, 1, NOW(), NOW(), 1),
-- 角色管理按钮权限
(8, 'role:add', '新增角色', '', 'sys:role:add', '/system/role', 'POST', 'RoleAdd', 7, 1, 3, 1, NOW(), NOW(), 1),
(9, 'role:edit', '编辑角色', '', 'sys:role:edit', '/system/role', 'PUT', 'RoleEdit', 7, 2, 3, 1, NOW(), NOW(), 1),
(10, 'role:delete', '删除角色', '', 'sys:role:delete', '/system/role', 'DELETE', 'RoleDelete', 7, 3, 3, 1, NOW(), NOW(), 1),
(11, 'role:query', '查询角色', '', 'sys:role:query', '/system/role', 'GET', 'RoleQuery', 7, 4, 3, 1, NOW(), NOW(), 1),
(12, 'role:assign', '分配角色', '', 'sys:role:assign', '/system/role/assign', 'POST', 'RoleAssign', 7, 5, 3, 1, NOW(), NOW(), 1),
-- 二级菜单：权限管理
(13, 'permission', '权限管理', 'lock', 'sys:permission', '/system/permission', 'GET', 'Permission', 1, 3, 1, 1, NOW(), NOW(), 1),
-- 权限管理按钮权限
(14, 'permission:add', '新增权限', '', 'sys:permission:add', '/system/permission', 'POST', 'PermissionAdd', 13, 1, 3, 1, NOW(), NOW(), 1),
(15, 'permission:edit', '编辑权限', '', 'sys:permission:edit', '/system/permission', 'PUT', 'PermissionEdit', 13, 2, 3, 1, NOW(), NOW(), 1),
(16, 'permission:delete', '删除权限', '', 'sys:permission:delete', '/system/permission', 'DELETE', 'PermissionDelete', 13, 3, 3, 1, NOW(), NOW(), 1),
(17, 'permission:query', '查询权限', '', 'sys:permission:query', '/system/permission', 'GET', 'PermissionQuery', 13, 4, 3, 1, NOW(), NOW(), 1),
(18, 'permission:assign', '分配权限', '', 'sys:permission:assign', '/system/permission/assign', 'POST', 'PermissionAssign', 13, 5, 3, 1, NOW(), NOW(), 1),
-- 二级菜单：登录日志
(19, 'loginlog', '登录日志', 'document', 'sys:loginlog', '/system/loginlog', 'GET', 'LoginLog', 1, 4, 1, 1, NOW(), NOW(), 1),
-- 登录日志按钮权限
(20, 'loginlog:query', '查询日志', '', 'sys:loginlog:query', '/system/loginlog', 'GET', 'LoginLogQuery', 19, 1, 3, 1, NOW(), NOW(), 1),
(21, 'loginlog:delete', '删除日志', '', 'sys:loginlog:delete', '/system/loginlog', 'DELETE', 'LoginLogDelete', 19, 2, 3, 1, NOW(), NOW(), 1),
(22, 'admin', '超级管理员权限', '', 'admin', '/admin', 'GET', 'Admin', 0, 0, 1, 1, NOW(), NOW(), 1);

-- ============================================
-- 4. 初始化用户角色关联
-- ============================================

INSERT INTO `sys_user_role` (`user_id`, `role_id`)
VALUES
(1, 1),  -- admin 用户拥有 SUPER_ADMIN 角色
(2, 3);  -- user 用户拥有 USER 角色

-- ============================================
-- 5. 初始化角色权限关联
-- ============================================

-- SUPER_ADMIN 角色拥有所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `sys_permission`;

-- USER 角色拥有基础查询权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
VALUES
(3, 2),   -- 用户管理菜单
(3, 6),   -- 查询用户
(3, 7),   -- 角色管理菜单
(3, 11),  -- 查询角色
(3, 13),  -- 权限管理菜单
(3, 17),  -- 查询权限
(3, 19),  -- 登录日志菜单
(3, 20);  -- 查询日志

-- ============================================
-- 6. 创建登录日志表（如果不存在）
-- ============================================

CREATE TABLE IF NOT EXISTS `sys_login_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户 ID',
  `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名',
  `status` TINYINT DEFAULT NULL COMMENT '登录状态（1 成功 0 失败）',
  `ip` VARCHAR(50) DEFAULT NULL COMMENT '登录 IP 地址',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '登录地点',
  `browser` VARCHAR(100) DEFAULT NULL COMMENT '浏览器类型',
  `os` VARCHAR(50) DEFAULT NULL COMMENT '操作系统',
  `msg` VARCHAR(255) DEFAULT NULL COMMENT '提示信息',
  `login_time` DATETIME DEFAULT NULL COMMENT '登录时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_username` (`username`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ============================================
-- 初始化完成
-- ============================================

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 默认账户信息
-- ============================================
-- 管理员账户:
--   用户名：admin
--   密码：admin123
--
-- 普通用户账户:
--   用户名：user
--   密码：admin123
-- ============================================
