package com.velvetalon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/18 11:34 : 创建文件
 */
@Data
public class ImageSenderTask {
    private String id;
    private String pixivId;
    private String accountCode;
    private String email;
    private Date createdTime;
    private Date sendTime;
}
