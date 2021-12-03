package com.velvetalon.listener;

import com.velvetalon.component.GlobalConfigConstants;
import com.velvetalon.component.ImagePrivateSender;
import com.velvetalon.entity.ImageSenderTask;
import com.velvetalon.mapper.GlobalConfigMapper;
import com.velvetalon.mapper.SuperAdminMapper;
import com.velvetalon.utils.MessageUtil;
import com.velvetalon.utils.UUIDUtil;
import love.forte.simbot.annotation.Filter;
import love.forte.simbot.annotation.OnGroup;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.filter.MatchType;
import net.mamoe.mirai.contact.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/2 19:19 : 创建文件
 */
@Component
public class PermissionOverloadListener {

    @Autowired
    private SuperAdminMapper superAdminMapper;

    @Autowired
    private GlobalConfigMapper globalConfigMapper;

    @OnGroup
    @Filter(value = "伊卡洛斯，启动权限过载", matchType = MatchType.EQUALS)
    public void func( GroupMsg groupMsg, MsgSender sender ){
        String accountCode = groupMsg.getAccountInfo().getAccountCode();
        if ("1".equals(superAdminMapper.isSuperAdmin(accountCode))) {
            if ("1".equals(globalConfigMapper.getConfigValue(GlobalConfigConstants.PERMISSION_OVERLOAD))) {
                MessageUtil.builder(groupMsg, "主...主人？", false, sender);
                return;
            }
            globalConfigMapper.setConfigValue(GlobalConfigConstants.PERMISSION_OVERLOAD, "1");

            MessageUtil.builder(groupMsg, "Type Alpha，战略用万能天使\"空之女王\"...", false, sender);
            MessageUtil.builder(groupMsg, "欢迎回来，Master", false, sender);
            return;
        }

        MessageUtil.builder(groupMsg, "冒充主人...不可原谅", false, sender);
        sender.SETTER.setGroupBan(groupMsg.getGroupInfo().getGroupCode(),
                groupMsg.getAccountInfo().getAccountCode(), 60, TimeUnit.SECONDS);
    }


    @OnGroup
    @Filter(value = "伊卡洛斯，关闭权限过载", matchType = MatchType.EQUALS)
    public void func2( GroupMsg groupMsg, MsgSender sender ){
        String accountCode = groupMsg.getAccountInfo().getAccountCode();
        if ("1".equals(superAdminMapper.isSuperAdmin(accountCode))) {
            if ("0".equals(globalConfigMapper.getConfigValue(GlobalConfigConstants.PERMISSION_OVERLOAD))) {
                MessageUtil.builder(groupMsg, "...？", false, sender);
                return;
            }
            globalConfigMapper.setConfigValue(GlobalConfigConstants.PERMISSION_OVERLOAD, "0");

            MessageUtil.builder(groupMsg, "等待您下次归来...", false, sender);
            return;
        }

        MessageUtil.builder(groupMsg, "冒充主人...不可原谅", false, sender);
        muteTarget(sender, groupMsg.getGroupInfo().getGroupCode(),
                groupMsg.getAccountInfo().getAccountCode(), 60, TimeUnit.SECONDS);

    }

    private boolean muteTarget( MsgSender sender, String groupCode, String accountCode, Integer time, TimeUnit timeUnit ){
        try {
            sender.SETTER.setGroupBan(groupCode, accountCode, time, timeUnit);
        } catch (PermissionDeniedException e) {
            return false;
        }
        return true;
    }
}
