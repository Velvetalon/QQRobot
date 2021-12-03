package com.velvetalon.listener;

import com.velvetalon.aspect.annotation.FunctionEnableCheck;
import com.velvetalon.mapper.HelpMapper;
import com.velvetalon.service.AutoReplyService;
import com.velvetalon.service.FunctionConfigService;
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
 * 2021/11/26 15:23 : 创建文件
 */
@Component
public class ConfigListener {


    @Autowired
    private AutoReplyService autoReplyService;

    @Autowired
    private FunctionConfigService functionConfigService;

    @OnGroup
    @FunctionEnableCheck(value = "CONFIG_MGMT", enable = true, adminRequired = true)
    @Filter(value = "#设置", matchType = MatchType.STARTS_WITH)
    public void func1( GroupMsg groupMsg, MsgSender sender ){
        functionConfigService.setFunctionStatus(groupMsg,sender);
    }

    @OnGroup
    @FunctionEnableCheck(value = "CONFIG_MGMT", enable = true, adminRequired = true)
    @Filter(value = "#查看设置", matchType = MatchType.STARTS_WITH)
    public void func2( GroupMsg groupMsg, MsgSender sender ){
        String msg = groupMsg.getMsg();
        String[] split = msg.split(" ");
        if(split.length < 2){
            MessageUtil.builder(groupMsg, "指令都打歪来我建议你还是不要聊秋秋了好吧", false, sender);
            return;
        }

        String groupCode = groupMsg.getGroupInfo().getGroupCode();
        String functionCode = msg.replaceFirst("#查看设置 ","");

        String value = functionConfigService.getFunctionValueByGroupCode(groupCode, functionCode);

        String result;
        if(value == null){
            result = "未检索到"+functionCode+"设置项。";
        }else{
            result =  functionCode + " 设置项检索完成，值："+value;
        }

        MessageUtil.builder(groupMsg, result , false, sender);
    }
}
