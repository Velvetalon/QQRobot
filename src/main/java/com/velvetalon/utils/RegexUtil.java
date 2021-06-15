package com.velvetalon.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @describe: 正则表达式工具类
 * @author: whc
 * HISTORY:
 * <p>
 * 2021/6/15 11:23 : 创建文件
 */
public class RegexUtil {
    private String pattern;

    private Pattern compile;

    private RegexUtil( String pattern ){
        this.pattern = pattern;
        this.compile = Pattern.compile(this.pattern);
    }

    public static RegexUtil getInstance( String pattern ){
        return new RegexUtil(pattern);
    }

    public List<String> getMatcherList(String content){
        Matcher matcher = this.compile.matcher(content);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }
}
