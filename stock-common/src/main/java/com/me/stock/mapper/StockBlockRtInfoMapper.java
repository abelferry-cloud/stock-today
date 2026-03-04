package com.me.stock.mapper;

import com.me.stock.pojo.domain.StockBlockDomain;
import com.me.stock.pojo.entity.StockBlockRtInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author Jovan
* @description 针对表【stock_block_rt_info(股票板块详情信息表)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.StockBlockRtInfo
*/
public interface StockBlockRtInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockBlockRtInfo record);

    int insertSelective(StockBlockRtInfo record);

    StockBlockRtInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockBlockRtInfo record);

    int updateByPrimaryKey(StockBlockRtInfo record);

    List<StockBlockDomain> sectorAllLimit(@Param("timePoint") Date lastDate);

    /**
     * 批量插入
     * @param list
     */
    void insertBatch(@Param("list") List<StockBlockRtInfo> list);

    /**
     * 查询指定时间点之后的板块数据
     * @param curDate
     * @return
     */
    List<StockBlockDomain> findBlockInfoByTimeLimit(Date curDate);
}
