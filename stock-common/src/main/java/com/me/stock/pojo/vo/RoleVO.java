package com.me.stock.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 所有角色信息
 */
@Schema(description = "所有角色信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleVO {

    /**
     * 角色id
     */
    @Schema(description = "角色id")
    private Long id;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;

    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String description;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Date updateTime;

    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private boolean isDeleted;
}
