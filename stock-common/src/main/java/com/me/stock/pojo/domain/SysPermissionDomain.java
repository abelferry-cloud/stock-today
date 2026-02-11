package com.me.stock.pojo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysPermissionDomain {
    /**
     * id
     */
    private Long id;

    /**
     * 权限名称
     */
    private String title;

    /**
     * 权限等级
     */
    private Integer level;
}
