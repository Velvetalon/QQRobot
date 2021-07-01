package com.velvetalon.housekeeper.listener;

import com.velvetalon.housekeeper.compoment.OnlineStatus;
import com.velvetalon.housekeeper.task.RobotRunningStatusDetection;
import lombok.SneakyThrows;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.annotation.Priority;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @describe: 监控文件类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/22 16:35 : 创建文件
 */
@Component
public class PrivateMsgListener {
    private static Logger logger = LogManager.getLogger(PrivateMsgListener.class);

    @Value("${monitor-resp}")
    private String monitorResp;

    @Value("${monitor-command}")
    private String monitorCommand;

    @Value("${monitor-target}")
    private String monitorTarget;

    @Autowired
    private RobotRunningStatusDetection robotRunningStatusDetection;

    @SneakyThrows
    @OnGroup
    @Filter(value = "#看看大笨蛋在吗", matchType = MatchType.EQUALS)
    public void func( GroupMsg groupMsg, MsgSender sender ){
        sender.SENDER.sendGroupMsg(groupMsg, "稍等————让我去康康！");
        logger.info("开始检测目标进程是否在线");

        OnlineStatus status = robotRunningStatusDetection.startDetection(monitorTarget, monitorCommand);

        switch (status) {
            case ONLINE:
                sender.SENDER.sendGroupMsg(groupMsg, "大笨蛋在线哦！");
                sender.SENDER.sendGroupMsg(groupMsg, "#还在吗");
                logger.info("目标在线。目标：" + monitorTarget);
                break;
            case OFFLINE_RESTART_SUCCESS:
                sender.SENDER.sendGroupMsg(groupMsg, "大笨蛋已经起床啦！");
                Thread.sleep(5000);
                sender.SENDER.sendGroupMsg(groupMsg, "#还在吗");
                logger.info("重启成功，目标：" + monitorTarget);
                break;
            case OFFLINE_RESTART_FAIL:  
                sender.SENDER.sendGroupMsg(groupMsg, "大笨蛋生病了！快去找主人！");
                logger.error("重启失败，目标：" + monitorTarget);
                break;
            default:
                break;
        }
    }

    /**
     * 接收到响应指令时取消重启任务
     *
     * @param privateMsg
     * @param sender
     */
    @OnPrivate
    public void func2( PrivateMsg privateMsg, MsgSender sender ){
        if (privateMsg.getMsg().trim().equals(monitorResp)) {
            logger.info(String.format("检测到取消重启任务指令，任务取消。来源：%s",
                    privateMsg.getAccountInfo().getAccountCode()));
            robotRunningStatusDetection.shutdownRestartTask(privateMsg.getAccountInfo().getAccountCode());
        }
    }
}
