package com.me.stock.utils;

import com.google.common.base.Strings;
import com.me.stock.pojo.domain.MenusPermDomain;
import com.me.stock.pojo.entity.SysPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 递归获取权限
 */
public class ParsePerm {
    public static List<String> getPermissions(List<SysPermission> permsByUserId){
        List<String> permissions=new ArrayList<>();
        permissions=permsByUserId.stream()
                .filter(per -> !Strings.isNullOrEmpty(per.getCode()) && per.getType() == 3)
                .map(per -> per.getCode()).collect(Collectors.toList());
       return permissions;
    }
    public static List<MenusPermDomain> digui(List<SysPermission> permsByUserId, Long pid){
        ArrayList<MenusPermDomain> list = new ArrayList<>();
        for (SysPermission p : permsByUserId) {
            if(p.getPid().equals(pid)){
                if(p.getType().intValue()!=3){
                    list.add(new MenusPermDomain(p.getId(), p.getTitle(), p.getIcon(), p.getUrl(), p.getName(), digui(permsByUserId,p.getId())));
                }
            }
        }
        return list;
    }
}