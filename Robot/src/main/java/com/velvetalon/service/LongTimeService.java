package com.velvetalon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.velvetalon.entity.AutoReplyEntity;
import com.velvetalon.entity.LongTimeEntity;

import java.util.List;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/30 10:38 : 创建文件
 */
public interface LongTimeService extends IService<LongTimeEntity> {
    /**
     * 随机获得bound张龙图
     *
     * @param bound
     * @return
     */
    List<LongTimeEntity> randomLong( int bound );

    /**
     * 在leftBound-rightBound范围内随机龙图
     *
     * @param leftBound
     * @param rightBound
     * @return
     */
    List<LongTimeEntity> randomLong( int leftBound, int rightBound );

    /**
     * 保存龙图
     *
     * @param accountCode
     * @param urlList
     * @return
     */
    int saveLong( String accountCode, String... urlList );

    /**
     * 逻辑删除龙图
     *
     * @param url
     */
    int removeLong( String url,String accountCode );
}
