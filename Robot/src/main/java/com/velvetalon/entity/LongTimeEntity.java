package com.velvetalon.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/30 10:29 : 创建文件
 */
@Data
@TableName("LONG_TIME")
public class LongTimeEntity {
    private String id;
    private String url;
    private String md5;
    private String addedBy;
    private Date addTime;
    private String deletedBy;
    private Date deleteTime;
    private String deleteFlag;
}
