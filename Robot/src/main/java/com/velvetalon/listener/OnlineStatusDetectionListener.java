package com.velvetalon.listener;

import com.velvetalon.utils.MessageUtil;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.Priority;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.api.sender.Sender;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/21 14:25 : 创建文件
 */
@Component
public class OnlineStatusDetectionListener {
    @OnGroup
    @Filter("#还在吗")
    public void func( GroupMsg groupMsg, MsgSender sender ){
        MessageContentBuilder builder = MessageUtil.builder();
        builder.face(17);
        sender.SENDER.sendGroupMsg(groupMsg, builder.build());
        builder = MessageUtil.builder();
        builder.text("我在！");
        sender.SENDER.sendGroupMsg(groupMsg, builder.build());
        builder = MessageUtil.builder();
        BufferedInputStream in = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("OnlineStatusResp.jpg"));
        builder.image(in);
        sender.SENDER.sendGroupMsg(groupMsg, builder.build());
    }
}
