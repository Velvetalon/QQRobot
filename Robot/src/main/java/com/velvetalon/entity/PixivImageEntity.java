package com.velvetalon.entity;

import lombok.Data;

/**
 * @describe: p站图片属性封装实体类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 14:14 : 创建文件
 */
@Data
public class PixivImageEntity {
    private String id;
    private String sourceUrl;
    private String previewUrl;
    private String url;
    private String title;
    private String member;
    private boolean r18;

}
