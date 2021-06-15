package com.velvetalon.listener;

import catcode.Neko;
import com.velvetalon.entity.AutoReplyEntity;
import com.velvetalon.service.AutoReplyService;
import com.velvetalon.utils.MessageUtil;
import com.velvetalon.utils.RegexUtil;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.containers.GroupAccountInfo;
import love.forte.simbot.api.message.containers.GroupInfo;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @describe: 自动回复方法。
 * @author: whc
 * HISTORY:
 * <p>
 * 2021/6/15 9:28 : 创建文件
 */
@Service
public class AutoReplyListener {

    private static final String AUTO_REPLY_PATTERN = "\\[[^\\:]+:{1}[^\\:]+]";

    private final RegexUtil regexUtil = RegexUtil.getInstance(AUTO_REPLY_PATTERN);

    @Autowired
    private AutoReplyService autoReplyService;

    @OnGroup
    @Filter(value = "#自动回复", matchType = MatchType.STARTS_WITH)
    public void func1( GroupMsg groupMsg, MsgSender sender ){
        String msg = groupMsg.getMsg();
        List<String> matcherList = regexUtil.getMatcherList(msg);
        if (matcherList.isEmpty()) {
            MessageUtil.builder(groupMsg,
                    "没有发现可以添加的自动回复哦！请检查你的语法是否有误！注意不要写成中文符号哦！", true, sender);
            return;
        }

        List<AutoReplyEntity> replyList = autoReplyService.parse(matcherList);
        int i = autoReplyService.saveReplyList(replyList, groupMsg.getGroupInfo().getGroupCode(), groupMsg.getAccountInfo().getAccountCode());
        String resp;
        if (i > 0) {
            resp = "成功啦！又学会了" + i + "个新回复！快来@我试试吧~";
        } else {
            resp = "完————全没能学会任何东西呢！快检查一下你是不是写错了什么！";
        }
        MessageUtil.builder(groupMsg, resp, true, sender);
    }


    @OnGroup
    @Filter(atBot = true)
    public void func2( GroupMsg groupMsg, MsgSender sender ){
        MessageContent msgContent = groupMsg.getMsgContent();
        List<Neko> cats = msgContent.getCats();
        for (Neko cat : cats) {
            if("text".equals(cat.getType())){
                String text = cat.get("text").trim();
                AutoReplyEntity reply = autoReplyService.getReply(groupMsg.getGroupInfo().getGroupCode(), text);
                if(reply != null){
                    MessageUtil.builder(groupMsg,reply.getReply(),true,sender);
                }
                return;
            }
        }
    }
}
