package com.velvetalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.velvetalon.entity.LongTimeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/30 10:37 : 创建文件
 */
@Mapper
public interface LongTimeMapper extends BaseMapper<LongTimeEntity> {
    /**
     * 随机龙图
     *
     * @param bound
     * @return
     */
    @Select("select * from LONG_TIME WHERE DELETE_FLAG = '0' ORDER BY rand() limit #{bound} ")
    List<LongTimeEntity> randomLong( int bound );
}
