package com.velvetalon.listener;

import catcode.Neko;
import com.velvetalon.entity.ImageSearchResult;
import com.velvetalon.service.impl.SearchImageService;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/8 16:22 : 创建文件
 */
@Service
public class SearchImageListener {

    private static Logger logger = LogManager.getLogger(SearchImageListener.class);

    /**
     * 注入得到一个消息构建器工厂。
     */
    @Autowired
    private MessageContentBuilderFactory messageBuilderFactory;

    @Value("${image-upload-retry}")
    private Integer retryLimit;

    /**
     * 监听搜图指令
     */
    @OnGroup
    @Filter(value = "#搜图", matchType = MatchType.STARTS_WITH)
    public void func1( GroupMsg groupMsg, MsgSender sender ){
        MessageContent msgContent = groupMsg.getMsgContent();
        List<Neko> cats = msgContent.getCats();
        for (Neko cat : cats) {
            if (cat.getType().equals("image")) {
                String url = cat.get("url");
                List<ImageSearchResult> results = SearchImageService.search(url);
                MessageContentBuilder builder = initMessageContentBuilder(groupMsg, results.isEmpty());
                int retry = 0;
                while (true) {
                    try {
                        for (ImageSearchResult result : results) {
                            builder.text("标题：" + result.getTitle() + "\n")
                                    .text("相似度：" + result.getSimilarity() + "\n")
                                    .imageUrl(result.getImageUrl())
                                    .text("Url：" + result.getUrl() + "\n")
                                    .text("作者：" + result.getMember() + "\n")
                                    .text("\n");
                        }

                        sender.SENDER.sendGroupMsg(groupMsg, builder.build());
                        break;
                    } catch (Exception e) {
                        logger.error("搜索图片出现异常，信息如下：");
                        logger.error(Arrays.toString(e.getStackTrace()));
                        if (retry++ < retryLimit) {
                            builder = initMessageContentBuilder(groupMsg, results.isEmpty());
                            continue;
                        } else {
                            builder = initMessageContentBuilder(groupMsg, "服务器异常，搜索失败，该图片可能被屏蔽！");
                        }
                    }
                }
            }
        }
    }

    private MessageContentBuilder initMessageContentBuilder( GroupMsg groupMsg, Boolean empty ){
        MessageContentBuilder builder = messageBuilderFactory.getMessageContentBuilder();
        builder.at(groupMsg.getAccountInfo());
        builder.text(empty ? "你搜索的图片没有找到！" : "\n你搜索的图片信息如下：\n\n");

        return builder;
    }

    private MessageContentBuilder initMessageContentBuilder( GroupMsg groupMsg, String msg ){
        MessageContentBuilder builder = messageBuilderFactory.getMessageContentBuilder();
        builder.at(groupMsg.getAccountInfo());
        builder.text(msg);

        return builder;
    }

}