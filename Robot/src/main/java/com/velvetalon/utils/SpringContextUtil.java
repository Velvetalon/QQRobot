package com.velvetalon.utils;

import com.velvetalon.QQRobotApplication;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @describe: Spring上下文工具类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 16:07 : 创建文件
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException{
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext(){
        return SpringContextUtil.applicationContext;
    }

    public static Object getBean( String name ){
        return applicationContext.getBean(name);
    }

    public static <T> T getBean( Class<T> clazz ){
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean( String name, Class<T> requiredType ){
        return applicationContext.getBean(name, requiredType);
    }

    public static boolean containsBean( String name ){
        return applicationContext.containsBean(name);
    }

    public static boolean isSingleton( String name ){
        return applicationContext.isSingleton(name);
    }

    public static Class<?> getType( String name ){
        return applicationContext.getType(name);
    }

    public static void applicationRestart( Class mainClass ){
        new Thread(() -> {
            ApplicationContext applicationContext = SpringContextUtil.getApplicationContext();
            ((ConfigurableApplicationContext) applicationContext).close();
            SpringApplication.run(mainClass);
        }).start();
    }
}