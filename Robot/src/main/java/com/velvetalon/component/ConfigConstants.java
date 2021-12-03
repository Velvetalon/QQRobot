package com.velvetalon.component;

import java.util.HashMap;
import java.util.Map;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/12/1 16:14 : 创建文件
 */
public class ConfigConstants {
    /**
     * 自动回复gcd
     */
    public static String REPLY_GCD = "REPLY_GCD";
    /**
     * 精确匹配概率
     */
    public static String EXACT_MATCH_PR = "EXACT_MATCH_PR";
    /**
     * 模糊匹配概率
     */
    public static String FUZZY_MATCH_PR = "FUZZY_MATCH_PR";

    public static final Map<String,String> DEFAULT_CONFIG = new HashMap<>();
    static{
        DEFAULT_CONFIG.put(REPLY_GCD,"5000");
        DEFAULT_CONFIG.put(EXACT_MATCH_PR,"50");
        DEFAULT_CONFIG.put(FUZZY_MATCH_PR,"25");
    }
}
