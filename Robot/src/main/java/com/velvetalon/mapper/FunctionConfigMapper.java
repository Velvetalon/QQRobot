package com.velvetalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.velvetalon.entity.AutoReplyEntity;
import com.velvetalon.entity.FunctionConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/11/25 17:12 : 创建文件
 */
@Mapper
public interface FunctionConfigMapper extends BaseMapper<FunctionConfig> {
}
