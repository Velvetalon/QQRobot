package com.velvetalon.mapper;

import catcode.NekoAibo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/1 15:51 : 创建文件
 */
@Mapper
public interface HelpMapper {
    /**
     * 查找帮助文档
     *
     * @return
     */
    @Select("select CONTENT from HELP limit 1")
    String getHelp();

    @Select("select CONTENT from COMMAND_EXAMPLE limit 1")
    String getCommandExample();
}
