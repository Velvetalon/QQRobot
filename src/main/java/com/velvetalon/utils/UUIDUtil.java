package com.velvetalon.utils;

import java.util.UUID;

/**
 * @describe: UUID工具类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/19 20:54 : 创建文件
 */
public class UUIDUtil {
    public static String next(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
