package com.velvetalon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.velvetalon.entity.AutoReplyEntity;
import com.velvetalon.mapper.AutoReplyMapper;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/15 14:50 : 创建文件
 */
public interface AutoReplyService extends IService<AutoReplyEntity> {
    /**
     * 传入字符串信息，处理为AutoReplayEntity列表后返回。
     * 传入参数必须为{@code "[text:text]" } 格式，推荐使用正则表达式提取
     * 关联工具类：{@link com.velvetalon.utils.RegexUtil}
     * 关联表达式：{@link com.velvetalon.listener.AutoReplyListener.AUTO_REPLY_PATTERN}
     *
     * @param matcherList
     * @return 当无法识别到有效格式时，返回空列表。如果格式有效，保证返回{@code  List<AutoReplyEntity>}
     */
    @NotEmpty
    List<AutoReplyEntity> parse( @NotNull @NotEmpty List<String> matcherList );

    /**
     * 保存
     * @param replyList
     * @return
     */
    int saveReplyList(@NotNull @NotEmpty List<AutoReplyEntity> replyList,String groupCode,String accountCode );

    /**
     * 获取回复
     * @param groupCode
     * @param trigger
     * @return
     */
    AutoReplyEntity getReply( String groupCode, String trigger );
}
