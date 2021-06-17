package com.velvetalon.utils;

import love.forte.simbot.api.message.MessageContentBuilder;
import love.forte.simbot.api.message.MessageContentBuilderFactory;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @describe:
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/15 11:52 : 创建文件
 */
@Component
public class MessageUtil {

    private static MessageContentBuilderFactory messageBuilderFactory;

    @Autowired
    private void setMessageBuilderFactory( MessageContentBuilderFactory messageBuilderFactory ){
        MessageUtil.messageBuilderFactory = messageBuilderFactory;
    }

    private MessageUtil(){
    }

    /**
     * 创建简单消息的工具方法。
     * @param groupMsg 消息实体。
     * @param msg 消息正文。
     * @param at 为指定是否@消息发送者。
     * @param sender 当sender不为空时，消息会被发送。
     * @return
     */
    public static MessageContentBuilder builder( GroupMsg groupMsg, String msg, boolean at, MsgSender sender ){
        MessageContentBuilder builder = messageBuilderFactory.getMessageContentBuilder();
        if (at) {
            builder.at(groupMsg.getAccountInfo());
        }
        if(StringUtil.hasValue(msg)){
            builder.text(msg);
        }
        if(sender != null){
            sender.SENDER.sendGroupMsg(groupMsg,builder.build());
        }
        return builder;
    }

    public static MessageContentBuilder builder( GroupMsg groupMsg, String msg, boolean at ){
        return builder(groupMsg,msg,at,null);
    }
}
