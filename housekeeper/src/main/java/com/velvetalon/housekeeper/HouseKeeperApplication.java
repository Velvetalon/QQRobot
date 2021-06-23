package com.velvetalon.housekeeper;

import love.forte.simbot.spring.autoconfigure.EnableSimbot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @describe: 监控器启动类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/22 16:17 : 创建文件
 */
@EnableSimbot
@EnableAsync
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class HouseKeeperApplication {
    public static void main( String[] args ){
        SpringApplication.run(HouseKeeperApplication.class, args);
    }
}