package com.velvetalon.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.velvetalon.entity.AutoReplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/15 14:47 : 创建文件
 */
@Mapper
public interface AutoReplyMapper extends BaseMapper<AutoReplyEntity> {
    /**
     * 获取一个模糊匹配后的结果
     *
     * @param groupCode
     * @param trigger
     * @return
     */
    @Select("call fuzzyMatching(#{groupCode} ,'1',#{trigger} );")
    String getOneFuzzyMatching( @Param("groupCode") String groupCode,
                                         @Param("trigger") String trigger );

    /**
     *
     * @param source
     * @param target
     * @param copyAll
     * @return
     */
    @Select("<script>" +
            "select * from AUTO_REPLY t1 WHERE group_number = #{source}  " +
            "<if test=\"!copyAll\"> AND ENABLE = 1</if>" +
            "AND NOT EXISTS ( select 1 from AUTO_REPLY t2 where t2.group_number = #{target} AND ( t2.ID = t1.ID or t2.SOURCE_ID = t1.ID) )" +
            "</script>")
    List<AutoReplyEntity> selectReplyEntity( @Param("source")String source,
                                             @Param("target")String target,
                                             @Param("copyAll")boolean copyAll );
}
