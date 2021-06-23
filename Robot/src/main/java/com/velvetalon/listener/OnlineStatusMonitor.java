package com.velvetalon.listener;


import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.Sender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @describe: 在线状态检测
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/23 14:28 : 创建文件
 */
@Component
public class OnlineStatusMonitor {
    private static Logger logger = LogManager.getLogger(OnlineStatusMonitor.class);

    @Value("${monitor-command}")
    private String monitorCommand;

    @Value("${monitor-resp}")
    private String monitorResp;

    @OnPrivate
    public void func1( PrivateMsg privateMsg, Sender sender ){
        if (privateMsg.getMsg().trim().equals(monitorCommand)) {
            logger.info(String.format("检测到在线质询，来源：%s，信息：%s已返回响应信息：%s",
                    privateMsg.getAccountInfo().getAccountCode(),
                    monitorCommand,
                    monitorResp));
            sender.sendPrivateMsg(privateMsg, monitorResp);
        }
    }
}
