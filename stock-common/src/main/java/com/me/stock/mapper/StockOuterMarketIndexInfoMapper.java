package com.me.stock.mapper;

import com.me.stock.pojo.domain.ExternalIndexDomain;
import com.me.stock.pojo.entity.StockOuterMarketIndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

/**
* @author 周俊远
* @description 针对表【stock_outer_market_index_info(外盘详情信息表)】的数据库操作Mapper
* @createDate 2025-09-12 17:25:42
* @Entity com.me.stock.pojo.entity.StockOuterMarketIndexInfo
*/
public interface StockOuterMarketIndexInfoMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StockOuterMarketIndexInfo record);

    int insertSelective(StockOuterMarketIndexInfo record);

    StockOuterMarketIndexInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StockOuterMarketIndexInfo record);

    int updateByPrimaryKey(StockOuterMarketIndexInfo record);

    /**
     * 获取外盘指数
     * @param endTime
     */
    List<ExternalIndexDomain> getExternalIndex();

    /**
     * 批量插入
     * @param entities  待插入对象
     * @return 插入数量
     */
    int insertBatch(@Param("entities") ArrayList<StockOuterMarketIndexInfo> entities);
}
