package com.velvetalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.velvetalon.entity.LongTimeEntity;
import com.velvetalon.listener.LongTime;
import com.velvetalon.listener.OnlineStatusMonitor;
import com.velvetalon.mapper.LongTimeMapper;
import com.velvetalon.service.LongTimeService;
import com.velvetalon.utils.HttpUtil;
import com.velvetalon.utils.Md5Util;
import com.velvetalon.utils.UUIDUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/30 10:39 : 创建文件
 */
@Component
@Transactional
public class LongTimeServiceImpl extends ServiceImpl<LongTimeMapper, LongTimeEntity>
        implements LongTimeService {
    private static Logger logger = LogManager.getLogger(LongTimeServiceImpl.class);

    @Autowired
    private LongTimeMapper longTimeMapper;

    @Override
    public List<LongTimeEntity> randomLong( int bound ){
        return longTimeMapper.randomLong(bound);
    }

    @Override
    public List<LongTimeEntity> randomLong( int leftBound, int rightBound ){
        return randomLong(new Random().nextInt(rightBound) + leftBound);
    }

    @Override
    public int saveLong( String accountCode, String... urlList ){
        int i = 0;
        for (String url : urlList) {
            QueryWrapper<LongTimeEntity> wrapper = new QueryWrapper<>();
            String md5;
            try {
                md5 = Md5Util.getMd5(HttpUtil.getInputStream(url, null, null));
            } catch (IOException e) {
                logger.error("获取龙图MD5失败，信息如下：");
                logger.error(e.getMessage());
                logger.error(e.getStackTrace());
                continue;
            }
            wrapper.eq("MD5", md5);
            wrapper.eq("DELETE_FLAG", "0");
            if (count(wrapper) > 0) {
                continue;
            }

            Date date = new Date();
            LongTimeEntity longTime = new LongTimeEntity();
            longTime.setId(UUIDUtil.next());
            longTime.setDeleteFlag("0");
            longTime.setAddTime(date);
            longTime.setAddedBy(accountCode);
            longTime.setUrl(url);
            try {
                longTime.setMd5(Md5Util.getMd5(HttpUtil.getInputStream(url, null, null)));
            } catch (IOException e) {
                logger.error("获取龙图MD5失败，信息如下：");
                logger.error(e.getMessage());
                logger.error(e.getStackTrace());
                continue;
            }
            i += save(longTime) ? 1 : 0;
        }

        return i;
    }

    @Override
    public int removeLong( String url, String accountCode ){
        int i = 0;
        String md5;
        try {
            md5 = Md5Util.getMd5(HttpUtil.getInputStream(url, null, null));
        } catch (IOException e) {
            logger.error("获取龙图MD5失败，信息如下：");
            logger.error(e.getMessage());
            logger.error(e.getStackTrace());
            return 0;
        }
        QueryWrapper<LongTimeEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("MD5", md5);
        wrapper.eq("DELETE_FLAG", "0");
        List<LongTimeEntity> list = list(wrapper);
        Date date = new Date();
        for (LongTimeEntity longTime : list) {
            longTime.setDeleteTime(date);
            longTime.setDeleteFlag("1");
            longTime.setDeletedBy(accountCode);
            i += updateById(longTime) ? 1 : 0;
        }
        return i;
    }
}
