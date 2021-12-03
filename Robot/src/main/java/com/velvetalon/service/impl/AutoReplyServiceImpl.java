package com.velvetalon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.velvetalon.component.ConfigConstants;
import com.velvetalon.entity.AutoReplyEntity;
import com.velvetalon.mapper.AutoReplyMapper;
import com.velvetalon.service.AutoReplyService;
import com.velvetalon.service.FunctionConfigService;
import com.velvetalon.utils.MessageUtil;
import com.velvetalon.utils.StringUtil;
import com.velvetalon.utils.UUIDUtil;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.sender.MsgSender;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/15 14:51 : 创建文件
 */
@Service
@Transactional
public class AutoReplyServiceImpl extends ServiceImpl<AutoReplyMapper, AutoReplyEntity> implements AutoReplyService {

    @Autowired
    private AutoReplyMapper autoReplyMapper;

    @Autowired
    private FunctionConfigService functionConfigService;

    private static final Map<String, Long> replyGcdMap = new HashMap<>();

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
            entity.setReply(split[1]);
            entity.setType(split[0].startsWith("%") && split[0].endsWith("%") ? "1" : "0");
            //检测是否为模糊匹配
            entity.setTriggerWord("1".equals(entity.getType()) ? split[0].replace("%", "") : split[0]);

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

//            QueryWrapper<AutoReplyEntity> wrapper = new QueryWrapper<>();
//            wrapper.eq("GROUP_NUMBER", groupCode);
//            wrapper.eq("TRIGGER_WORD", entity.getTriggerWord());
//            wrapper.eq("ENABLE", 1);
//            if (autoReplyMapper.selectCount(wrapper) > 0) {
//                AutoReplyEntity update = autoReplyMapper.selectOne(wrapper);
//                update.setDisabledBy(accountCode);
//                update.setDisableTime(date);
//                update.setEnable(0);
//                autoReplyMapper.updateById(update);
//            }
            count += autoReplyMapper.insert(entity);
        }
        return count;
    }

    @Override
    public AutoReplyEntity getReply( String groupCode, String trigger ){
        //gcd判定
        long gcd = Long.parseLong(functionConfigService.getFunctionValueByGroupCode(groupCode, ConfigConstants.REPLY_GCD));
        Long lastReplyTime = replyGcdMap.get(groupCode);
        if(lastReplyTime != null && System.currentTimeMillis() - lastReplyTime<= gcd){
            return null;
        }

        QueryWrapper<AutoReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("GROUP_NUMBER", groupCode);
        wrapper.eq("TRIGGER_WORD", trigger);
        wrapper.eq("ENABLE", 1);
//        wrapper.eq("TYPE", 0);
        List<AutoReplyEntity> result = autoReplyMapper.selectList(wrapper);
        if (result.size() > 0) {
            //精确匹配判定
            int exactPr = Integer.parseInt(functionConfigService.getFunctionValueByGroupCode(groupCode, ConfigConstants.EXACT_MATCH_PR));
            if(exactPr > 100){
                exactPr = exactPr % 100;
            }
            if(new Random().nextInt(100) < exactPr){
                replyGcdMap.put(groupCode,System.currentTimeMillis());
                return result.get(new Random().nextInt(result.size()));
            }
            return null;
        }

        //模糊匹配判定
        int fuzzy = Integer.parseInt(functionConfigService.getFunctionValueByGroupCode(groupCode, ConfigConstants.FUZZY_MATCH_PR));
        if(fuzzy > 100){
            fuzzy = fuzzy % 100;
        }
        if(new Random().nextInt(100) < fuzzy){
            replyGcdMap.put(groupCode,System.currentTimeMillis());
            return autoReplyMapper.selectById(autoReplyMapper.getOneFuzzyMatching(groupCode, trigger));
        }

        return null;
    }

    @Override
    public int removeReply( String reply, String groupCode, String accountCode ){
        Date date = new Date();
        int count = 0;
        QueryWrapper<AutoReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("GROUP_NUMBER", groupCode);
        wrapper.eq("REPLY", reply);
        wrapper.eq("ENABLE", 1);
        if (autoReplyMapper.selectCount(wrapper) > 0) {
            List<AutoReplyEntity> updateList = autoReplyMapper.selectList(wrapper);
            for (AutoReplyEntity update : updateList) {
                update.setDisabledBy(accountCode);
                update.setDisableTime(date);
                update.setEnable(0);
                count += autoReplyMapper.updateById(update);
            }
        }
        return count;
    }

    @Override
    public void copyThesaurus( GroupMsg groupMsg, MsgSender sender, String source, boolean copyAll ){
        if(copyAll){
            MessageUtil.builder(groupMsg,
                    "你解放了黑暗之力...", false, sender);
        }else{
            MessageUtil.builder(groupMsg,
                    "黑暗仪式开始了...", false, sender);
        }

//        QueryWrapper<AutoReplyEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("GROUP_NUMBER", source);
//        if (!copyAll) {
//            queryWrapper.eq("ENABLE", 1);
//        }
        int count = 0;
        int disableCount = 0;
        String groupCode = groupMsg.getGroupInfo().getGroupCode();
        String accountCode = groupMsg.getAccountInfo().getAccountCode();
        Date date = new Date();
        List<AutoReplyEntity> replyList = autoReplyMapper.selectReplyEntity(source,groupCode,copyAll);
        if(replyList.isEmpty()){
            MessageUtil.builder(groupMsg,
                    "卑微的凡人！这个位面尽是一片虚无！你会为此付出代价！！！", false, sender);
            return;
        }

        MessageUtil.builder(groupMsg,
                "感应到了"+replyList.size()+"个虚空生物的坐标。它们来了...", false, sender);

        for (AutoReplyEntity entity : replyList) {
            count+=1;
            disableCount += entity.getEnable() == 0 ? 1 : 0;
            entity.setSourceId(StringUtils.isEmpty(entity.getSourceId()) ? entity.getId() : entity.getSourceId());
            entity.setId(UUIDUtil.next());
            entity.setEnable(1);
            entity.setGroupNumber(groupCode);
            entity.setCreatedBy(accountCode);
            entity.setCreateTime(date);
            autoReplyMapper.insert(entity);
        }
        MessageUtil.builder(groupMsg,
                "卑微的凡人，你成功从编号" + groupCode + "的虚空位面召唤了" + count + "个生物来到现世...", false, sender);
        if(copyAll){
            MessageUtil.builder(groupMsg,
                    "不幸的是，由于你的亵渎之举，有" + disableCount + "个生物混在其中...", false, sender);
        }
        MessageUtil.builder(groupMsg,
                "好好享受吧...", false, sender);
    }

    @Override
    public int cleanTrigger( String trigger, String groupCode, String accountCode ){
        QueryWrapper<AutoReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("GROUP_NUMBER", groupCode);
        wrapper.eq("ENABLE", 1);
        if (trigger.startsWith("%") && trigger.endsWith("%")) {
            wrapper.like("TRIGGER_WORD", trigger);
        } else {
            wrapper.eq("TRIGGER_WORD", trigger);
        }


        AutoReplyEntity entity = new AutoReplyEntity();
        entity.setEnable(0);
        entity.setDisableTime(new Date());
        entity.setDisabledBy(accountCode);

        return autoReplyMapper.update(entity, wrapper);
    }

    @Override
    public int cleanReply( String reply, String groupCode, String accountCode ){
        QueryWrapper<AutoReplyEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("GROUP_NUMBER", groupCode);
        wrapper.eq("ENABLE", 1);
        if (reply.startsWith("%") && reply.endsWith("%")) {
            wrapper.like("REPLY", reply);
        } else {
            wrapper.eq("REPLY", reply);
        }


        AutoReplyEntity entity = new AutoReplyEntity();
        entity.setEnable(0);
        entity.setDisableTime(new Date());
        entity.setDisabledBy(accountCode);

        return autoReplyMapper.update(entity, wrapper);
    }
}
