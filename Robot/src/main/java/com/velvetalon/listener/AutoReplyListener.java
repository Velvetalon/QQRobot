package com.velvetalon.listener;

import catcode.Neko;
import com.velvetalon.aspect.annotation.FunctionEnableCheck;
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
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @describe: 自动回复方法。
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/15 9:28 : 创建文件
 */
@Component
public class AutoReplyListener {

    private static final String AUTO_REPLY_PATTERN = "\\[[^\\:]+:{1}[^\\:]+]";

    private final RegexUtil regexUtil = RegexUtil.getInstance(AUTO_REPLY_PATTERN);

    @Autowired
    private AutoReplyService autoReplyService;

    @OnGroup
    @FunctionEnableCheck("AUTO_REPLY")
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
            resp = "成功啦！又学会了" + i + "个新回复！快来试试吧~";
        } else {
            resp = "完————全没能学会任何东西呢！快检查一下你是不是写错了什么！";
        }
        MessageUtil.builder(groupMsg, resp, true, sender);
    }


    @OnGroup
    @FunctionEnableCheck("AUTO_REPLY")
    @Filter(atBot = false)
    public void func2( GroupMsg groupMsg, MsgSender sender ){
        MessageContent msgContent = groupMsg.getMsgContent();
        List<Neko> cats = msgContent.getCats();
        for (Neko cat : cats) {
            if ("text".equals(cat.getType())) {
                String text = cat.get("text").trim();
                try {
                    AutoReplyEntity reply = autoReplyService.getReply(groupMsg.getGroupInfo().getGroupCode(), text);
                    if (reply != null) {
                        MessageUtil.builder(groupMsg, reply.getReply(), false, sender);
                    }
                }catch (Exception e){
                    MessageUtil.builder(groupMsg, "检测到回复机制出现异常，请检查参数状态并立即使用以下指令关闭自动回复：#设置 AUTO_REPLY 0", false, sender);
                }
                return;
            }
        }
    }

    @OnGroup
    @FunctionEnableCheck(value = "AUTO_REPLY")
    @Filter(value = "#删除回复", matchType = MatchType.STARTS_WITH)
    public void func3( GroupMsg groupMsg, MsgSender sender ){
        String msg = groupMsg.getMsg();
        if (msg.split(" ").length < 2) {
            MessageUtil.builder(groupMsg,
                    "指令都打歪来你是不是罗志啊", true, sender);
            return;
        }
        int i = autoReplyService.removeReply(msg.replaceFirst("#清空回复 ",""), groupMsg.getGroupInfo().getGroupCode(), groupMsg.getAccountInfo().getAccountCode());
        String resp;
        if (i > 0) {
            resp = "成功啦！移除" + i + "个自动回复！";
        } else {
            resp = "完————全没移除任何东西呢！快检查一下你是不是写错了什么！";
        }
        MessageUtil.builder(groupMsg, resp, true, sender);
    }

    @OnGroup
    @FunctionEnableCheck(value = "AUTO_REPLY", adminRequired = true)
    @Filter(value = "#清空触发", matchType = MatchType.STARTS_WITH)
    public void func4( GroupMsg groupMsg, MsgSender sender ){
        String resp;

        String msg = groupMsg.getMsg();
        String[] split = msg.split(" ");
        if (split.length < 2) {
            resp = "指令都打歪来你到底会不会用啊";
        } else {
            int count = autoReplyService.cleanTrigger(msg.replaceFirst("#清空触发 ",""),
                    groupMsg.getGroupInfo().getGroupCode(),
                    groupMsg.getAccountInfo().getAccountCode());
            resp = "一共" + count + "条数据全都清空咯！";
        }
        MessageUtil.builder(groupMsg, resp, true, sender);
    }
    @OnGroup
    @FunctionEnableCheck(value = "AUTO_REPLY", adminRequired = true)
    @Filter(value = "#清空回复", matchType = MatchType.STARTS_WITH)
    public void func5( GroupMsg groupMsg, MsgSender sender ){
        String resp;

        String msg = groupMsg.getMsg();
        String[] split = msg.split(" ");
        if (split.length < 2) {
            resp = "指令都打歪来你到底会不会用啊";
        } else {
            int count = autoReplyService.cleanReply(msg.replaceFirst("#清空回复 ",""),
                    groupMsg.getGroupInfo().getGroupCode(),
                    groupMsg.getAccountInfo().getAccountCode());
            resp = "一共" + count + "条数据全都清空咯！";
        }
        MessageUtil.builder(groupMsg, resp, true, sender);
    }

}
