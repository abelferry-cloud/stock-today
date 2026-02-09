package com.me.stock.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiOperation(value = "更新用户角色参数")
public class UpdateUserRoleReqVo {
    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 角色ID列表
     */
    @ApiModelProperty(value = "角色ID列表")
    private List<Long> roleIds;
}
