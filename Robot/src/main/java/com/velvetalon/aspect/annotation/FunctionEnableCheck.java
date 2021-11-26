package com.velvetalon.aspect.annotation;

import java.lang.annotation.*;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/11/25 16:37 : 创建文件
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FunctionEnableCheck {
    String value();

    boolean ENABLE() default false;
}
