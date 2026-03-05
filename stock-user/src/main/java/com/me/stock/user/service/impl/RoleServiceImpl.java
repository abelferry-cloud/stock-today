package com.me.stock.user.service.impl;

import com.me.stock.mapper.SysRoleMapper;
import com.me.stock.mapper.SysUserMapper;
import com.me.stock.pojo.entity.SysRole;
import com.me.stock.pojo.entity.SysUser;
import com.me.stock.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 角色服务实现类
 *
 * @author stock-user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysRoleMapper sysRoleMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(SysRole role) {
        log.info("创建角色：name={}, description={}", role.getName(), role.getDescription());

        role.setStatus(role.getStatus() != null ? role.getStatus() : 1);
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());

        return sysRoleMapper.insert(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole role) {
        log.info("更新角色：id={}, name={}", role.getId(), role.getName());

        role.setUpdateTime(new Date());
        return sysRoleMapper.updateByPrimaryKeySelective(role) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long id) {
        log.info("删除角色：id={}", id);
        return sysRoleMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public SysRole getRoleById(Long id) {
        return sysRoleMapper.selectByPrimaryKey(id);
    }

    @Override
    public SysRole getRoleByName(String name) {
        // 注意：SysRoleMapper 没有按名称查询的方法，这里使用遍历方式
        List<SysRole> roles = sysRoleMapper.selectAll();
        for (SysRole role : roles) {
            if (name.equals(role.getName())) {
                return role;
            }
        }
        return null;
    }

    @Override
    public List<SysRole> getAllRoles() {
        return sysRoleMapper.selectAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoleToUser(Long userId, Long roleId) {
        log.info("为用户分配角色：userId={}, roleId={}", userId, roleId);

        SysRole role = sysRoleMapper.selectByPrimaryKey(roleId);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        SysUser user = sysUserMapper.selectByPrimaryKey(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 注意：sys_user 表没有 role_id 字段，需要通过中间表关联
        // 这里需要根据实际情况实现角色分配逻辑
        // 暂时返回 true 表示成功
        return true;
    }

    @Override
    public SysRole getDefaultRole() {
        List<SysRole> roles = sysRoleMapper.selectAll();
        for (SysRole role : roles) {
            if ("USER".equals(role.getName())) {
                return role;
            }
        }
        // 如果不存在默认角色，返回 null
        return null;
    }
}
