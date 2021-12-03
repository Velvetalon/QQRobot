package com.velvetalon.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/3 15:55 : 创建文件
 */
@Mapper
public interface SuperAdminMapper {
    @Select("select 1 from SUPER_ADMIN where ACCOUNT_CODE = #{code} ")
    String isSuperAdmin(@Param("code") String code);
}
