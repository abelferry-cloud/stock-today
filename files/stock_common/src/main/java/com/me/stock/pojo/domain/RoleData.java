package com.me.stock.pojo.domain;

import com.me.stock.pojo.vo.RoleVO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 封装角色数据
 */
@ApiOperation(value = "封装角色数据")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleData {
    /**
     * 用户具有的角色数据
     */
    @ApiModelProperty(value = "用户具有的角色数据")
    private List<Long> ownRoleIds;

    /**
     * 所有角色数据
     */
    @ApiModelProperty(value = "所有角色数据")
    private List<RoleVO> allRole;
}
