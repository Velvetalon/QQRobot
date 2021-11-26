package com.velvetalon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.velvetalon.entity.AutoReplyEntity;
import com.velvetalon.entity.FunctionConfig;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/11/25 17:11 : 创建文件
 */
public interface FunctionConfigService extends IService<FunctionConfig> {
    /**
     * 设置功能启用
     * @param groupMsg
     * @param sender
     * @param enable
     */
    void setFunctionStatus( GroupMsg groupMsg, MsgSender sender, boolean enable );
}
