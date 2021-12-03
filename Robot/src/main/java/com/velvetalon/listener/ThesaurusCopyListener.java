package com.velvetalon.listener;

import com.velvetalon.aspect.annotation.FunctionEnableCheck;
import com.velvetalon.service.AutoReplyService;
import com.velvetalon.utils.MessageUtil;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/2 14:06 : 创建文件
 */
@Component
public class ThesaurusCopyListener {

    @Autowired
    private AutoReplyService autoReplyService;

    @OnGroup
    @FunctionEnableCheck(value = "THESAURUS_COPY", adminRequired = true)
    @Filter(value = "#虚空召唤", matchType = MatchType.STARTS_WITH)
    public void func1( GroupMsg groupMsg, MsgSender sender ){
        String msg = groupMsg.getMsg();

        String[] split = msg.split(" ");

        if(split.length < 2){
            MessageUtil.builder(groupMsg,
                    "打字都打歪来你先去学打字好不好", false, sender);
            return;
        }
        try{
            Long.valueOf(split[1]);
        }catch (Exception e){
            MessageUtil.builder(groupMsg,
                    "这是群号吗你在整我是吗", false, sender);
            return;
        }

        if(groupMsg.getGroupInfo().getGroupCode().equals(split[1])){
            MessageUtil.builder(groupMsg,
                    "搁这原地TP是吧？", false, sender);
            return;
        }

        autoReplyService.copyThesaurus(groupMsg,sender, split[1],
                split.length > 2 && split[2].equals("原初解放"));
    }

}
