package com.velvetalon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/3 15:57 : 创建文件
 */
@Mapper
public interface GlobalConfigMapper {

    /**
     * 查找全局值
     * @param configName
     * @return
     */
    @Select("select CONFIG_VALUE from GLOBAL_CONFIG where CONFIG_NAME = #{configName} ")
    String getConfigValue( @Param("configName") String configName);

    @Update("update GLOBAL_CONFIG SET CONFIG_VALUE = #{configValue}  where CONFIG_NAME = #{configName} ")
    int setConfigValue( @Param("configName") String configName,
                        @Param("configValue") String configValue );
}
