package com.velvetalon.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.velvetalon.entity.ImageSenderTask;
import com.velvetalon.listener.SearchImageListener;
import com.velvetalon.utils.*;
import love.forte.simbot.bot.BotManager;
import org.apache.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/18 11:32 : 创建文件w
 */
@Component
public class ImagePrivateSender implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LogManager.getLogger(SearchImageListener.class);

    @Autowired
    private BotManager manager;

    private static final Queue<ImageSenderTask> TASK_QUEUE = new LinkedBlockingQueue<>();

    private static final String IMAGE_INFO_API = "https://www.pixiv.net/ajax/illust/%s";

    @Autowired
    private CookieManager cookieManager;

    @Autowired
    private HttpProxyManager httpProxyManager;

    @Value("${image-cache}")
    private String imageCache;

    @Value("${image-upload-retry}")
    private Integer retryLimit;

    @Value("${private-image-sender}")
    private Integer privateImageSender;

    @Value("${email.username}")
    private String emailUsername;

    @Value("${email.auth-code}")
    private String emailAuthCode;

    @Override
    public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent ){
        for (int i = 0; i < privateImageSender; i++) {
            AsyncWrapper.submit(this::sender);
        }
    }

    public static void addTask( ImageSenderTask task ){
        TASK_QUEUE.add(task);
    }

    private void sender(){
        ImageSenderTask task;
        while (true) {
            if ((task = TASK_QUEUE.poll()) == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                continue;
            }

            String originalUrl = getOriginalUrl(task.getPixivId());
            if (originalUrl == null) {
                MessageUtil.sendPrivateMsg(manager.getDefaultBot().getSender().SENDER,
                        task.getAccountCode(),
                        String.format("%s没能找到任何图片哦！", task.getPixivId()));
            }

            MessageUtil.sendPrivateMsg(manager.getDefaultBot().getSender().SENDER,
                    task.getAccountCode(),
                    "正在为你下载图片哦！");

            Map<String, String> header = new HashMap<>();
            header.put("Referer", "https://www.pixiv.net/");
            int i = 0;
            List<String> list = new ArrayList<>();
            while (true) {
                String imagePath;
                String fileName = buildFileName(task.getPixivId());
                try {
                    imagePath = HttpUtil.download(String.format(originalUrl, i++),
                            httpProxyManager.getHttpProxy(),
                            header,
                            fileName,
                            imageCache);
                    if (imagePath == null) {
                        break;
                    }

                    ImageUtil.checkCompleteImageWithException(imagePath);
                    list.add(imagePath);
                } catch (Exception e) {
                    logger.error("下载文件失败，信息如下：");
                    logger.error(e);
                    logger.error(StringUtil.array2String(e.getStackTrace()));
                }
            }

            if (list.isEmpty()) {
                MessageUtil.sendPrivateMsg(manager.getDefaultBot().getSender().SENDER,
                        task.getAccountCode(),
                        String.format("%s下载全部失败啦！也许你可以向主人投诉以帮助我变得更好×", task.getPixivId()));
                return;
            }

            EmailUtil.sendMail(
                    emailUsername, emailAuthCode,
                    task.getEmail(),
                    "我是蒸饭机器人！你订阅的图片送来啦！",
                    "你的图片~！",
                    list
            );
            MessageUtil.sendPrivateMsg(manager.getDefaultBot().getSender().SENDER,
                    task.getAccountCode(),
                    "您订阅的图片已经发送到你的邮箱！请注意查收啦！没找到的话说不定在垃圾箱里哦？");
        }
    }

    private String getOriginalUrl( String pid ){
        HttpResponse resp = HttpUtil.get(String.format(IMAGE_INFO_API, pid),
                null,
                cookieManager.getCookieMap(),
                httpProxyManager.getHttpProxy(), null);
        cookieManager.updateByHeaders(resp.getHeaders("set-cookie"));
        JSONObject json;
        try {
            json = JSON.parseObject(StreamUtil.readAll(resp.getEntity().getContent()));
        } catch (IOException e) {
            logger.warn("图片详情获取失败，信息如下：");
            logger.warn(e.getMessage());
            logger.warn(e);
            return null;
        }

        String original = json.getJSONObject("body").getJSONObject("urls").getString("original");
        return original.replace("p0", "p%s");
    }

    private String buildFileName( String pixivId ){
        return "Pid" + pixivId + "." + UUID.randomUUID() + ".jpg";
    }
}
