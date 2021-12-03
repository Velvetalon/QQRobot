package com.velvetalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.velvetalon.component.ConfigConstants;
import com.velvetalon.entity.AutoReplyEntity;
import com.velvetalon.entity.FunctionConfig;
import com.velvetalon.mapper.AutoReplyMapper;
import com.velvetalon.mapper.FunctionConfigMapper;
import com.velvetalon.service.AutoReplyService;
import com.velvetalon.service.FunctionConfigService;
import com.velvetalon.utils.MessageUtil;
import com.velvetalon.utils.UUIDUtil;
import love.forte.simbot.api.message.assists.Permissions;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.List;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/11/25 17:11 : 创建文件
 */
@Service
public class FunctionConfigServiceImpl extends ServiceImpl<FunctionConfigMapper, FunctionConfig> implements FunctionConfigService {
    @Autowired
    private FunctionConfigMapper functionConfigMapper;

    @Override
    public void setFunctionStatus( GroupMsg groupMsg, MsgSender sender ){
        String msg = groupMsg.getMsg();
        String[] msgArr = msg.split(" ");
        if (msgArr.length < 3) {
            MessageUtil.sendGroup(sender.SENDER, groupMsg, "发指令又不说什么事，我真怀疑某些人闲的程度啊",
                    0, null);
            return;
        }

        String functionCode = msgArr[1];
        String value = msgArr[2];

        int count = 0;
        UpdateWrapper<FunctionConfig> wrapper = new UpdateWrapper<>();
        QueryWrapper<FunctionConfig> qw = new QueryWrapper<>();
        qw.clear();
        qw.eq("FUNCTION_CODE",functionCode);
        qw.eq("GROUP_NUMBER", groupMsg.getGroupInfo().getGroupCode());
        List<FunctionConfig> r = functionConfigMapper.selectList(qw);
        if (r.isEmpty()) {
            FunctionConfig config = new FunctionConfig();
            config.setId(UUIDUtil.next());
            config.setFunctionCode(functionCode);
            config.setGroupNumber(groupMsg.getGroupInfo().getGroupCode());
            config.setFunctionValue(value);
            functionConfigMapper.insert(config);
        }

        wrapper.clear();
        wrapper.set("FUNCTION_VALUE", value);
        wrapper.eq("FUNCTION_CODE", functionCode);
        wrapper.eq("GROUP_NUMBER", groupMsg.getGroupInfo().getGroupCode());
        count += functionConfigMapper.update(null, wrapper);

        MessageUtil.sendGroup(sender.SENDER, groupMsg, count + "个功能改好咯~",
                0, null);
    }

    @Override
    public String getFunctionValueByGroupCode( String groupCode, String functionCode ){
        QueryWrapper<FunctionConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("FUNCTION_CODE",functionCode);
        queryWrapper.eq("GROUP_NUMBER",groupCode);
        List<FunctionConfig> configList = functionConfigMapper.selectList(queryWrapper);
        if(configList.isEmpty()){
            if(ConfigConstants.DEFAULT_CONFIG.containsKey(functionCode)){
                FunctionConfig insert = new FunctionConfig();
                insert.setGroupNumber(groupCode);
                insert.setFunctionCode(functionCode);
                insert.setFunctionValue(ConfigConstants.DEFAULT_CONFIG.get(functionCode));
                functionConfigMapper.insert(insert);
                return insert.getFunctionValue();
            }else{
                return null;
            }
        }
        return configList.get(0).getFunctionValue();
    }
}
