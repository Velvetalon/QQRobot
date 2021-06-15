package com.velvetalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.velvetalon.entity.AutoReplyEntity;
import com.velvetalon.mapper.AutoReplyMapper;
import com.velvetalon.service.AutoReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @describe: 文件描述
 * @author: whc
 * HISTORY:
 * <p>
 * 2021/6/15 14:51 : 创建文件
 */
@Service
@Transactional
public class AutoReplyServiceImpl extends ServiceImpl<AutoReplyMapper, AutoReplyEntity> implements AutoReplyService {

    @Autowired
    private AutoReplyMapper autoReplyMapper;

    @Override
    public List<AutoReplyEntity> parse( List<String> matcherList ){
        List<AutoReplyEntity> result = new ArrayList<>();
        for (String str : matcherList) {
            assert str.startsWith("[") && str.endsWith("]") && str.contains(":");

            String[] split = str.substring(1, str.length() - 1).split(":");
            if (split.length != 2) {
                continue;
            }
            AutoReplyEntity entity = new AutoReplyEntity();
            entity.setTriggerWord(split[0]);
            entity.setReply(split[1]);
            result.add(entity);
        }
        return result;
    }

    @Override
    public int saveReplyList( List<AutoReplyEntity> replyList, String groupCode, String accountCode ){
        Date date = new Date();
        int count = 0;
        for (AutoReplyEntity entity : replyList) {
            entity.setEnable(1);
            entity.setGroupNumber(groupCode);
            entity.setCreatedBy(accountCode);
            entity.setCreateTime(date);

            QueryWrapper<AutoReplyEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("GROUP_NUMBER", groupCode);
            wrapper.eq("TRIGGER_WORD", entity.getTriggerWord());
            wrapper.eq("ENABLE", 1);
            if (autoReplyMapper.selectCount(wrapper) > 0) {
                AutoReplyEntity update = autoReplyMapper.selectOne(wrapper);
                update.setDisabledBy(accountCode);
                update.setDisableTime(date);
                update.setEnable(0);
                autoReplyMapper.updateById(update);
            }
            count += autoReplyMapper.insert(entity);
        }
        return count;
    }

    @Override
    public AutoReplyEntity getReply( String groupCode, String trigger ){
        QueryWrapper<AutoReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("GROUP_NUMBER", groupCode);
        wrapper.eq("TRIGGER_WORD", trigger);
        wrapper.eq("enable",1);
        List<AutoReplyEntity> result = autoReplyMapper.selectList(wrapper);
        if(result.size() > 0){
            // 这里将来考虑支持在多个选项中随机回复一条
            return result.get(0);
        }
        return null;
    }
}
