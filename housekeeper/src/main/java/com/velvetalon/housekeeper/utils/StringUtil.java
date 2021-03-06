package com.velvetalon.housekeeper.utils;

/**
 * @describe: 字符串工具类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 11:57 : 创建文件
 */
public class StringUtil {
    private StringUtil(){}

    public static boolean hasValue( String s ){
        return s != null && s.trim().length() > 0 ;
    }

    public static String getValue(String s){
        return s == null ? "" : s.trim();
    }

    public static String array2String(Object[] array){
        StringBuilder sb = new StringBuilder();
        for (Object o : array) {
            sb.append(o.toString()+"\n");
        }
        return sb.toString();
    }
}
