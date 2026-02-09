package com.me.stock.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 多条件综合查询用户请求参数封装
 */
@ApiModel(description = "多条件综合查询用户请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMConditionReqVO {

    //分页参数
    @ApiModelProperty("分页参数")
    private Integer pageNum;

    //分页大小
    @ApiModelProperty("分页大小")
    private Integer pageSize;

    //用户姓名
    @ApiModelProperty("用户姓名")
    private String userName;

    //真实姓名
    @ApiModelProperty("真实姓名")
    private String nickName;

    //开始时间
    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date startTime;

    //结束时间
    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date endTime;

}
