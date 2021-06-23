package com.velvetalon.listener;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @describe: 随机图片监听器。
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 11:50 : 创建文件
 */
@Service
public class RandomImageListener {

    private static Logger logger = LogManager.getLogger(RandomImageListener.class);

    @Autowired
    private PixivRequestManager pixivRequestManager;

    @Autowired
    private HttpProxyManager HttpProxyManager;

    private ReentrantLock lock = new ReentrantLock();

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

    @Value("${image-download-retry}")
    private Long imageDownloadRetry;

    private Map<String, Long> cache = new HashMap<>();

    @OnGroup
    @Filter(value = "#来点", matchType = MatchType.STARTS_WITH)
    public void func1( GroupMsg groupMsg, MsgSender sender ){
        if (!checkDelay(groupMsg.getGroupInfo().getGroupCode())) {
            return;
        }


        String msg = groupMsg.getMsg().trim();
        boolean r18 = msg.endsWith("色图");

        String keyword = msg.replace("#来点", "").replace("色图", "");
        if (!StringUtil.hasValue(keyword)) {
            MessageUtil.builder(groupMsg, "想看什么图的话，可以告诉我！没有图想看的话就不要骚扰人家了啦！", true, sender);
            return;
        }

        MessageUtil.builder(groupMsg, String.format("我要去给你找%s的图片啦！先消失一会儿~", keyword, groupDelay), true, sender);

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

        builder = MessageUtil.builder(groupMsg, null, false);
        List<String> imageCacheList = new ArrayList<>();

        for (PixivImageEntity entity : result) {
            String cacheName = UUID.randomUUID() + ".jpg";
            String imageLocal = null;
            // 在启用了source-image选项后，将会发送原图
            for (int i = 0; i < imageDownloadRetry; i++) {
                try {

                    imageLocal = HttpUtil.download(sourceImage ? entity.getSourceUrl() : entity.getPreviewUrl(),
                            HttpProxyManager.getHttpProxy(),
                            header,
                            cacheName, imageCache);
                    ImageUtil.checkCompleteImageWithException(imageLocal);
                } catch (Exception e) {
                    logger.error("下载文件出现异常，信息如下：");
                    logger.error(e.getMessage());
                    logger.error(StringUtil.array2String(e.getStackTrace()));
                    continue;
                }

            }
            if (!ImageUtil.isCompleteImage(imageLocal)) {
                continue;
            }

            if (r18) {
                String confuseFile = ImageUtil.confuse(imageLocal, imageCache);
                imageCacheList = ImageUtil.splitImage(confuseFile, imageCache, splitNumber);
            } else {
                imageCacheList = new ArrayList<>();
                imageCacheList.add(imageLocal);
            }

            builder
                    .text("标题：" + entity.getTitle() + "\n")
                    .text("Pid：" + entity.getId() + "\n");
            for (String cache : imageCacheList) {
                builder.imageLocal(cache).text("\n");
            }

            builder
                    .text("作者：" + entity.getMember() + "\n")
                    .text("Url：" + entity.getUrl()+"\n\n");
        }
        builder.text("你要的图找来啦！一共给你发了" + result.size() + "张图喔！");
        int retry = 0;
        while (true) {
            try {
                Thread.sleep(2000);
                sender.SENDER.sendGroupMsg(groupMsg, builder.build());
                break;
            } catch (Exception e) {
                logger.error("上传图片出现异常，信息如下：");
                logger.error(StringUtil.array2String(e.getStackTrace()));
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

    private boolean checkDelay( String groupCode ){
        if (cache.get(groupCode) == null || System.currentTimeMillis() - cache.get(groupCode) > groupDelay * 1000) {
            cache.put(groupCode, System.currentTimeMillis());
            return true;
        }
        return false;
    }
}

