package com.velvetalon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/11/25 17:10 : 创建文件
 */
@Data
@TableName("FUNCTION_CONFIG")
public class FunctionConfig {
    @TableId
    private String id;
    private String groupNumber;
    private String functionCode;
    private String enable;
}