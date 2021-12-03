package com.velvetalon.listener;

import catcode.Neko;
import com.velvetalon.aspect.annotation.FunctionEnableCheck;
import com.velvetalon.entity.AutoRepeatEntity;
import com.velvetalon.utils.MessageUtil;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/1 15:30 : 创建文件
 */
@Component
public class AutoRepeatListener {
    private Map<String, AutoRepeatEntity> map = new HashMap<>();

    @OnGroup
    @FunctionEnableCheck("AUTO_REPEAT")
    public void func1( GroupMsg groupMsg, MsgSender sender ){
//        MessageContent msgContent = groupMsg.getMsgContent();
//        for (Neko cat : msgContent.getCats()) {
//            if(!cat.getType().equals("text")){
//                return;
//            }
//        }

        String msg = groupMsg.getMsg();
        if(checkRepeat(groupMsg.getGroupInfo().getGroupCode(),msg)){
            MessageUtil.builder(groupMsg, msg, false, sender);
        }
    }

    private boolean checkRepeat(String groupCode,String msg){
        if(!map.containsKey(groupCode)){
            AutoRepeatEntity bean = new AutoRepeatEntity();
            bean.setGroupCode(groupCode);
            map.put(groupCode,bean);
        }
        AutoRepeatEntity bean = map.get(groupCode);
        boolean flag = false;
        if(msg.equals(bean.getLastMsg()) && !msg.equals(bean.getLastRepeat())){
            flag = true;
        }
        bean.setLastMsg(msg);
        if(flag){
            bean.setLastRepeat(msg);
        }

        return flag;
    }
}
