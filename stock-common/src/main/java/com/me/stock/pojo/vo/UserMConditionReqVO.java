package com.me.stock.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 多条件综合查询用户请求参数封装
 */
@Schema(description = "多条件综合查询用户请求参数")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMConditionReqVO {

    //分页参数
    @Schema(description = "分页参数")
    private Integer pageNum;

    //分页大小
    @Schema(description = "分页大小")
    private Integer pageSize;

    //用户姓名
    @Schema(description = "用户姓名")
    private String userName;

    //真实姓名
    @Schema(description = "真实姓名")
    private String nickName;

    //开始时间
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date startTime;

    //结束时间
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date endTime;

}
