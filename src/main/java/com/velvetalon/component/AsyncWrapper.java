package com.velvetalon.component;

import com.velvetalon.utils.SpringContextUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @describe: 异步代码包装类。用于执行无法被spring管理的类中需要异步执行的方法。
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 16:00 : 创建文件
 */
@Component
public class AsyncWrapper {

    @Async
    public void async( Supplier supplier){
        supplier.get();
    }

    public static void submit( Supplier supplier){
        SpringContextUtil.getBean(AsyncWrapper.class).async(supplier);
    }
}
