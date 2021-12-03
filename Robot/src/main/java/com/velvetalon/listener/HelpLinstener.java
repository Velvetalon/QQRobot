package com.velvetalon.listener;

import com.velvetalon.mapper.HelpMapper;
import com.velvetalon.utils.MessageUtil;
import com.velvetalon.utils.TextToImageUtil;
import io.ktor.client.features.Sender;
import lombok.SneakyThrows;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.*;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/1 15:49 : 创建文件
 */
@Component
public class HelpLinstener {
    @Autowired
    private HelpMapper helpMapper;

    @SneakyThrows
    @OnGroup
    @Filter(value = "#帮助", matchType = MatchType.EQUALS)
    public void func1( GroupMsg groupMsg, MsgSender sender ){

        File tempFile = File.createTempFile("tmp", ".png");
        FileOutputStream os = new FileOutputStream(tempFile);
        TextToImageUtil.createImage(helpMapper.getHelp().split("\n"), new Font("微软雅黑", Font.PLAIN, 15),
                 Integer.MAX_VALUE, 25, os);
        MessageContentBuilder builder = MessageUtil.builder();
        builder.image(new FileInputStream(tempFile));
        MessageUtil.sendGroup(sender.SENDER, groupMsg, builder.build(), 0, null);
        tempFile.delete();
    }


    @SneakyThrows
    @OnGroup
    @Filter(value = "#常用命令", matchType = MatchType.EQUALS)
    public void func2( GroupMsg groupMsg, MsgSender sender ){
        File tempFile = File.createTempFile("tmp", ".png");
        FileOutputStream os = new FileOutputStream(tempFile);
        TextToImageUtil.createImage(helpMapper.getCommandExample().split("\n"), new Font("微软雅黑", Font.PLAIN, 15),
                Integer.MAX_VALUE, 25, os);
        MessageContentBuilder builder = MessageUtil.builder();
        builder.image(new FileInputStream(tempFile));
        MessageUtil.sendGroup(sender.SENDER, groupMsg, builder.build(), 0, null);
        tempFile.delete();
    }
}
