package com.velvetalon.listener;

import com.velvetalon.aspect.annotation.FunctionEnableCheck;
import com.velvetalon.entity.AutoReplyEntity;
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

import java.util.List;

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
    @FunctionEnableCheck(value = "CONFIG_ENABLE",ENABLE = true)
    @Filter(value = "#启用", matchType = MatchType.STARTS_WITH)
    public void func1( GroupMsg groupMsg, MsgSender sender ){
        functionConfigService.setFunctionStatus(groupMsg,sender,true);
    }

    @OnGroup
    @FunctionEnableCheck(value = "CONFIG_ENABLE",ENABLE = true)
    @Filter(value = "#关闭", matchType = MatchType.STARTS_WITH)
    public void func2( GroupMsg groupMsg, MsgSender sender ){
        functionConfigService.setFunctionStatus(groupMsg,sender,false);
    }
}
