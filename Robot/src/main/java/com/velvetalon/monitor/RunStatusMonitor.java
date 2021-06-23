package com.velvetalon.monitor;

import com.velvetalon.QQRobotApplication;
import com.velvetalon.listener.RandomImageListener;
import com.velvetalon.utils.SpringContextUtil;
import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/23 10:44 : 创建文件
 */
@Component
public class RunStatusMonitor implements ApplicationListener<ContextRefreshedEvent> {
    private static Logger logger = LogManager.getLogger(RunStatusMonitor.class);

    @Value("${restart-command}")
    private String restartCommand;

    @Value("${monitor-port}")
    private Integer monitorPort;


    @SneakyThrows
    @Override
    public void onApplicationEvent( ContextRefreshedEvent contextRefreshedEvent ){
        ServerSocket serverSocket = new ServerSocket(monitorPort);
        while (true) {
            Socket client = serverSocket.accept();
            BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
            try {
                if (restartCommand.equals(br.readLine())) {
                    logger.info("检测到重启指令，服务即将重启。");
                    SpringContextUtil.applicationRestart(QQRobotApplication.class);
                    break;
                }
            } finally {
                br.close();
                client.close();
                serverSocket.close();
            }
        }

    }
}
