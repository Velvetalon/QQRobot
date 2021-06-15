package com.velvetalon.entity;

import lombok.Data;

/**
 * @describe: 文件描述
 * @author: whc
 * HISTORY:
 * <p>
 * 2021/6/10 9:50 : 创建文件
 */
@Data
public class ImageSearchResult {
    private String title;
    private String url;
    private String imageUrl;
    private String similarity;
    private String member;
}
