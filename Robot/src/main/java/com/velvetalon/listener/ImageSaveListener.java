package com.velvetalon.listener;

import com.velvetalon.aspect.annotation.FunctionEnableCheck;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import org.springframework.stereotype.Component;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/11/26 17:14 : 创建文件
 */
@Component
public class ImageSaveListener {

    @OnGroup
    @FunctionEnableCheck(value = "UPLOAD_FILE", enable = true)
    @Filter(value = "#上传图片", matchType = MatchType.STARTS_WITH)
    public void func1( GroupMsg groupMsg, MsgSender sender ){
    }

}
