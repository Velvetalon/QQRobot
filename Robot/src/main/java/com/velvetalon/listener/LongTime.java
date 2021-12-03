package com.velvetalon.listener;

import catcode.Neko;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.velvetalon.aspect.annotation.FunctionEnableCheck;
import com.velvetalon.entity.LongTimeEntity;
import com.velvetalon.service.LongTimeService;
import com.velvetalon.utils.MessageUtil;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @describe: 龙图time
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/30 10:26 : 创建文件
 */
@Component
public class LongTime {
    private static Logger logger = LogManager.getLogger(OnlineStatusMonitor.class);

    @Autowired
    private LongTimeService longTimeService;

    @Value("${image-upload-retry}")
    private Integer retryLimit;

    @Value("${image-cache}")
    private String imageCache;

    @OnGroup
    @FunctionEnableCheck(value = "LONG_TIME")
    @Filter(value = "#新龙图", matchType = MatchType.STARTS_WITH)
    public void func( GroupMsg groupMsg, MsgSender sender ){
        int i = 0;
        for (Neko cat : groupMsg.getMsgContent().getCats("image")) {
            String url = cat.get("url");
            QueryWrapper<LongTimeEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("URL", url);
            if (longTimeService.count(wrapper) > 0) {
                continue;
            }
            i += longTimeService.saveLong(groupMsg.getAccountInfo().getAccountCode(), url);
        }
        MessageContentBuilder builder;
        if (i > 0) {
            builder = MessageUtil.builder(groupMsg,
                    String.format("我又学会了%s张龙图！快#龙time试一试！Its long time\n", i),
                    true);
        } else {
            builder = MessageUtil.builder(groupMsg,
                    "你 根本不龙\n",
                    true);
        }
        List<LongTimeEntity> list = longTimeService.randomLong(1);
        if (!list.isEmpty()) {
            builder.image(list.get(0).getUrl());
        }
        MessageUtil.sendGroup(sender.SENDER, groupMsg, builder.build(), retryLimit, logger);
    }

    @OnGroup
    @FunctionEnableCheck(value = "LONG_TIME")
    @Filter(value = "#这个不是龙", matchType = MatchType.STARTS_WITH)
    public void func2( GroupMsg groupMsg, MsgSender sender ){
        int count = 0;
        for (Neko cat : groupMsg.getMsgContent().getCats("image")) {
            String url = cat.get("url");
            count = longTimeService.removeLong(url, groupMsg.getAccountInfo().getAccountCode());
        }
        MessageContentBuilder builder;
        if (count == 0) {
            builder = MessageUtil.builder(groupMsg,
                    String.format("你 根本不龙\n"),
                    true);
        } else {
            builder = MessageUtil.builder(groupMsg,
                    String.format("知道啦！\n"),
                    true);
        }

        List<LongTimeEntity> list = longTimeService.randomLong(1);
        if (!list.isEmpty()) {
            builder.image(list.get(0).getUrl());
        }
        MessageUtil.sendGroup(sender.SENDER, groupMsg, builder.build(), retryLimit, logger);
    }

    @OnGroup
    @FunctionEnableCheck(value = "LONG_TIME")
    @Filter(value = "#龙time", matchType = MatchType.STARTS_WITH)
    public void func3( GroupMsg groupMsg, MsgSender sender ){
        MessageContentBuilder builder = MessageUtil.builder();
        List<LongTimeEntity> list = longTimeService.randomLong(3);
        if (!list.isEmpty()) {
            for (LongTimeEntity longTimeEntity : list) {
                builder.image(longTimeEntity.getUrl());
                builder.text("\n");
            }
        }
        MessageUtil.sendGroup(sender.SENDER, groupMsg, builder.build(), retryLimit, logger);
    }
}
