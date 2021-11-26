package com.velvetalon.aspect.aspect;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.velvetalon.aspect.annotation.FunctionEnableCheck;
import com.velvetalon.entity.FunctionConfig;
import com.velvetalon.service.FunctionConfigService;
import com.velvetalon.utils.MessageUtil;
import love.forte.simbot.api.message.MessageContent;
import love.forte.simbot.api.message.events.GroupMsg;
import love.forte.simbot.api.message.events.MessageGet;
import love.forte.simbot.api.message.events.PrivateMsg;
import love.forte.simbot.api.sender.MsgSender;
import love.forte.simbot.api.sender.Sender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/11/25 16:41 : 创建文件
 */
@Component
@Aspect
public class FuncEnableCheckAspect {

    private static Logger logger = LogManager.getLogger(FuncEnableCheckAspect.class);

    @Value("${image-upload-retry}")
    private Integer retryLimit;

    @Autowired
    private FunctionConfigService functionConfigService;

//    @Pointcut(value = "execution(@annotation(com.velvetalon.aspect.annotation.FunctionEnableCheck))")
//    public void funcEnableCheckAspectPointCut() {
//
//    }

    @Around(value = "@annotation(com.velvetalon.aspect.annotation.FunctionEnableCheck)")
    public Object doAround( ProceedingJoinPoint pjp ) throws Throwable{
        Object[] args = pjp.getArgs();
        if (args.length < 2 || !(args[0] instanceof MessageGet) || !(args[1] instanceof MsgSender)) {
            return pjp.proceed();
        }

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        FunctionEnableCheck annotation = signature.getMethod().getAnnotation(FunctionEnableCheck.class);
        if(annotation.ENABLE()){
            return pjp.proceed();
        }

        String functionCode = annotation.value();
        QueryWrapper<FunctionConfig> wrapper = new QueryWrapper<>();
        wrapper.eq("FUNCTION_CODE", functionCode);
        wrapper.eq("GROUP_NUMBER",getNumber((MessageGet) args[0]));
        List<FunctionConfig> list = functionConfigService.list(wrapper);
        if(list.size() > 0 && "0".equals(list.get(0).getEnable())){
            return null;
        }
        return pjp.proceed();
    }

    private void sendMsg( MessageGet messageGet, Sender sender, String msg){
        if(messageGet instanceof GroupMsg){
            GroupMsg groupMsg = (GroupMsg) messageGet;
            MessageContent content = MessageUtil.builder().text(msg).build();
            MessageUtil.sendGroup(sender,groupMsg,content,retryLimit,logger);
        }
        if(messageGet instanceof PrivateMsg){
            PrivateMsg privateMsg = (PrivateMsg) messageGet;
            MessageUtil.sendPrivateMsg(sender,privateMsg.getAccountInfo().getAccountCode(),msg);
        }
    }

    private String getNumber(MessageGet messageGet){
        if(messageGet instanceof GroupMsg){
            GroupMsg groupMsg = (GroupMsg) messageGet;
            return groupMsg.getGroupInfo().getGroupCode();
        }
        if(messageGet instanceof PrivateMsg){
            PrivateMsg privateMsg = (PrivateMsg) messageGet;
            return privateMsg.getAccountInfo().getAccountCode();
        }
        return null;
    }

}
