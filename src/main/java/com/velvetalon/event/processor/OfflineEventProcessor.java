package com.velvetalon.event.processor;

import love.forte.simbot.annotation.Listen;
import love.forte.simbot.api.message.events.MsgGet;
import love.forte.simbot.component.mirai.message.event.LoginAble;
import love.forte.simbot.component.mirai.message.event.MiraiBotOffline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @describe: 离线事件处理器
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/18 14:28 : 创建文件
 */
@Component
public class OfflineEventProcessor  {
    private static final Logger logger = LogManager.getLogger(OfflineEventProcessor.class);

    @Listen(MiraiBotOffline.class)
    public void loginAsync( MsgGet msgGet,LoginAble loginAble ){
        if(msgGet instanceof MiraiBotOffline.Active){
            logger.warn("主动离线");
        }
        if(msgGet instanceof MiraiBotOffline.Force  ){
            logger.warn("被挤下线");
        }
        if(msgGet instanceof MiraiBotOffline.Dropped  ){
            logger.warn("网络原因离线");
        }
        if(msgGet instanceof MiraiBotOffline.Other  ){
            logger.warn("其他原因离线");
        }

        logger.warn(msgGet.toString());
        logger.warn(msgGet.getClass());

        logger.warn("正在重新登陆");
        int i = 0;
        while (true){
            try{
                Thread.sleep(5000);
                loginAble.loginBlocking();
                break;
            }catch (Exception e){
                logger.warn(String.format("第%s次重新登入失败！！！",i++));
                logger.warn(e.getMessage());
                logger.warn(e.getStackTrace());
            }
        }
        logger.warn("重新登录成功");
    }
}
