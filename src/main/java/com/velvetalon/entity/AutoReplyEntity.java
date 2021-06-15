package com.velvetalon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @describe: 自动回复实体类
 * @author: whc
 * HISTORY:
 * <p>
 * 2021/6/15 9:47 : 创建文件
 */
@Data
@TableName("AUTO_REPLY")
public class AutoReplyEntity {
    @TableId
    private String id;
    private String triggerWord;
    private String reply;
    private String groupNumber;
    private Integer enable;
    private String createdBy;
    private Date createTime;
    private String disabledBy;
    private Date disableTime;
}
