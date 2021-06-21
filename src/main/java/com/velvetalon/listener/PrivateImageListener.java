package com.velvetalon.listener;

import catcode.Neko;
import com.velvetalon.component.ImagePrivateSender;
import com.velvetalon.entity.ImageSenderTask;
import com.velvetalon.utils.MessageUtil;
import com.velvetalon.utils.UUIDUtil;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.annotation.OnPrivate;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @describe: 私聊图片发送器
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/19 1:06 : 创建文件
 */
@Component
public class PrivateImageListener {

    @OnGroup
    @Filter(value = "#给我也整一个", matchType = MatchType.STARTS_WITH)
    public void func( GroupMsg groupMsg, MsgSender sender ){


        String[] split = groupMsg.getMsg().split(" ");
        int success = 0;
        for (int i = 1; i < split.length; i++) {
            String pid;
            try {
                pid = String.valueOf(Integer.valueOf(split[i]));
            } catch (NumberFormatException ignored) {
                continue;
            }
            ImageSenderTask task = new ImageSenderTask();
            task.setId(UUIDUtil.next());
            task.setCreatedTime(new Date());
            task.setAccountCode(groupMsg.getAccountInfo().getAccountCode());
            task.setPixivId(pid);
            task.setEmail(groupMsg.getAccountInfo().getAccountCode() + "@qq.com");
            ImagePrivateSender.addTask(task);
            success++;
        }
        MessageUtil.builder(groupMsg,
                success == 0
                        ? "没有找到任何图片！快看看你是不是写错了什么！"
                        : String.format("收到~稍后会有%s张图片发送到%s哟~！如果还没加好友的话，快点加我好友吧！",
                        success, groupMsg.getAccountInfo().getAccountCode() + "@qq.com"),
                true,
                sender);

    }

    @OnPrivate
    public void func2( PrivateMsg groupMsg, MsgSender sender ){
    }
}
