package com.velvetalon.listener;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.velvetalon.component.HttpProxyManager;
import com.velvetalon.component.PixivRequestManager;
import com.velvetalon.entity.PixivImageEntity;
import com.velvetalon.utils.HttpUtil;
import com.velvetalon.utils.ImageUtil;
import com.velvetalon.utils.MessageUtil;
import com.velvetalon.utils.StringUtil;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @describe: 随机图片监听器。
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 11:50 : 创建文件
 */
@Service
public class RandomImageListener {

    private static Logger logger = LoggerFactory.getLogger(RandomImageListener.class);

    @Autowired
    private PixivRequestManager pixivRequestManager;

    @Autowired
    private HttpProxyManager HttpProxyManager;

    @Value("${image-upload-retry}")
    private Integer retryLimit;

    @Value("${split-number}")
    private Integer splitNumber;

    @Value("${image-cache}")
    private String imageCache;

    @Value("${source-image}")
    private boolean sourceImage;

    @Value("${group-delay}")
    private Long groupDelay;

    private LoadingCache<String, Object> cache;

    @OnGroup
    @Filter(value = "#来点", matchType = MatchType.STARTS_WITH)
    public void func1( GroupMsg groupMsg, MsgSender sender ){
        if (!checkDelay(groupMsg.getGroupInfo().getGroupCode())) {
            MessageUtil.builder(groupMsg, "CD还没转好！再等等啦！", true, sender);
            return;
        }

        String msg = groupMsg.getMsg().trim();
        boolean r18 = msg.endsWith("色图");

        String keyword = msg.replace("#来点", "").replace("色图", "");
        if (!StringUtil.hasValue(keyword)) {
            MessageUtil.builder(groupMsg, "想看什么图的话，可以告诉我！没有图想看的话就不要骚扰人家了啦！", true, sender);
            return;
        }

        List<PixivImageEntity> result;
        try {
            result = pixivRequestManager.search(keyword, r18);
        } catch (UnsupportedEncodingException e) {
            MessageUtil.builder(groupMsg, "不要输入奇怪的东西啊kora！", true, sender);
            return;
        }

        if (result.isEmpty()) {
            MessageUtil.builder(groupMsg, "一张都没找到喔！", true, sender);
            return;
        }
        MessageContentBuilder builder;
        // java写爬虫真的很麻烦
        Map<String, String> header = new HashMap<>();
        header.put("Referer", "https://www.pixiv.net/");

        for (PixivImageEntity entity : result) {
            String cacheName = UUID.randomUUID() + ".jpg";
            String imageLocal;
            List<String> imageCacheList;
            try {
                // 在启用了source-image选项后，将会发送原图
                imageLocal = HttpUtil.download(sourceImage ? entity.getSourceUrl() : entity.getPreviewUrl(),
                        HttpProxyManager.getHttpProxy(),
                        header,
                        cacheName, imageCache);

                if (r18) {
                    imageCacheList = ImageUtil.splitImage(imageLocal, imageCache, splitNumber);
                } else {
                    imageCacheList = new ArrayList<>();
                    imageCacheList.add(imageLocal);
                }
            } catch (Exception e) {
                //下载失败暂时不处理
                logger.error("下载图片出现异常，信息如下：");
                logger.error(Arrays.toString(e.getStackTrace()));
                continue;
            }


            builder = MessageUtil.builder(groupMsg, null, false);
            builder
                    .text("标题：" + entity.getTitle() + "\n");
            for (String cache : imageCacheList) {
                builder.imageLocal(cache).text("\n");
            }

            builder
                    .text("作者：" + entity.getMember() + "\n")
                    .text("Url：" + entity.getUrl());
            int retry = 0;
            while (true) {
                try {
                    sender.SENDER.sendGroupMsg(groupMsg, builder.build());
                    break;
                } catch (Exception e) {
                    logger.error("随机图片出现异常，信息如下：");
                    logger.error(Arrays.toString(e.getStackTrace()));
                    if (retry++ < retryLimit) {
                        continue;
                    }
                    break;
                }
            }
            for (String cache : imageCacheList) {
                new File(cache).deleteOnExit();
            }
        }
        MessageUtil.builder(groupMsg, "你要的图找来啦！一共给你发了" + result.size() + "张图喔！", true, sender);
    }

    @PostConstruct
    private void initCache(){
        cache = CacheBuilder.newBuilder().
                initialCapacity(10).// 缓存容器的初始容量
                maximumSize(10000).// 缓存容器最大缓存容量
                expireAfterAccess(groupDelay, TimeUnit.SECONDS).  // 当缓存想在指定的时间段内没有被读或写就会被回收
                build(new CacheLoader<String, Object>() {
            @Override
            public String load( String key ){
                return null;
            }
        });
    }

    private boolean checkDelay( String groupCode ){
        if (cache.getIfPresent(groupCode) == null) {
            cache.put(groupCode,0);
            return true;
        }
        return false;
    }
}

