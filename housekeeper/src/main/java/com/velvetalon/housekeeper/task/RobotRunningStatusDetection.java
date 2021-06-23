package com.velvetalon.housekeeper.task;

import com.velvetalon.housekeeper.compoment.OnlineStatus;
import com.velvetalon.housekeeper.utils.SockUtil;
import com.velvetalon.housekeeper.utils.StringUtil;
import love.forte.simbot.bot.BotManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/23 14:36 : 创建文件
 */
@Component
public class RobotRunningStatusDetection {
    private static Logger logger = LogManager.getLogger(RobotRunningStatusDetection.class);

    @Value("${monitor-timeout}")
    private Long monitorTimeout;

    @Value("${monitor-port}")
    private Integer monitorPort;

    @Value("${restart-command}")
    private String restartCommand;

    private static final Map<String, Boolean> restartTaskMap = new HashMap<>();

    @Autowired
    private BotManager botManager;

    /**
     * 开始一次检测任务。向指定QQ号发送响应指令，
     * 目标必须在{@link RobotRunningStatusDetection.monitorTimeout} 内响应，
     * 并通过{@link RobotRunningStatusDetection.shutdownRestartTask} 接口取消重启任务。
     * 在到达timeout指定的时间后任务仍未被取消，则尝试发送重启指令。
     *
     * @param target         检测目标的QQ号
     * @param monitorCommand 检测指令
     * @return
     */
    public OnlineStatus startDetection( String target, String monitorCommand ){
        try {
            botManager.getDefaultBot().getSender().SENDER.sendPrivateMsg(
                    target, monitorCommand
            );
            restartTaskMap.put(target, true);
            Thread.sleep(monitorTimeout * 1000);
            if (restartTaskMap.get(target) != null && restartTaskMap.get(target)) {
                SockUtil.sendTcpCommand("127.0.0.1", monitorPort, restartCommand);
                logger.info("唤醒成功，目标：" + target);
                return OnlineStatus.OFFLINE_RESTART_SUCCESS;
            } else {
                logger.info("目标在线，无需唤醒。目标：" + target);
                return OnlineStatus.ONLINE;
            }
        } catch (Exception e) {
            logger.error("唤醒失败！相关信息如下：");
            logger.error(e.getMessage());
            logger.error(StringUtil.array2String(e.getStackTrace()));
            return OnlineStatus.OFFLINE_RESTART_FAIL;
        }
    }

    public void shutdownRestartTask( String target ){
        restartTaskMap.put(target, false);
    }
}
