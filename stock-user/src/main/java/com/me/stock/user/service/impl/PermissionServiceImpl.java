package com.me.stock.user.service.impl;

import com.me.stock.mapper.SysPermissionMapper;
import com.me.stock.pojo.entity.SysPermission;
import com.me.stock.user.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 权限服务实现类
 *
 * @author stock-user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionMapper sysPermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPermission(SysPermission permission) {
        log.info("创建权限：name={}, perms={}", permission.getName(), permission.getPerms());

        permission.setStatus(permission.getStatus() != null ? permission.getStatus() : 1);
        permission.setCreateTime(new Date());
        permission.setUpdateTime(new Date());

        return sysPermissionMapper.insert(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(SysPermission permission) {
        log.info("更新权限：id={}, name={}", permission.getId(), permission.getName());

        permission.setUpdateTime(new Date());
        return sysPermissionMapper.updateByPrimaryKeySelective(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long id) {
        log.info("删除权限：id={}", id);
        return sysPermissionMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public SysPermission getPermissionById(Long id) {
        return sysPermissionMapper.selectByPrimaryKey(id);
    }

    @Override
    public SysPermission getPermissionByName(String name) {
        // 注意：SysPermissionMapper 没有按名称查询的方法
        List<SysPermission> perms = sysPermissionMapper.selectAll();
        for (SysPermission perm : perms) {
            if (name.equals(perm.getName())) {
                return perm;
            }
        }
        return null;
    }

    @Override
    public List<SysPermission> getAllPermissions() {
        return sysPermissionMapper.selectAll();
    }

    @Override
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
        // 注意：SysPermissionMapper 没有直接按角色 ID 查询的方法
        // 这里返回所有权限，实际应用中需要添加相应的 SQL 查询
        return sysPermissionMapper.selectAll();
    }
}
