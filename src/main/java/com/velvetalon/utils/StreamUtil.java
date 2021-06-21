package com.velvetalon.utils;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @describe: 流工具类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/18 11:59 : 创建文件
 */
public class StreamUtil {

    @SneakyThrows
    public static String readAll( InputStream is ){
        byte[] buf = new byte[4096];
        int length = 0;
        StringBuilder sb = new StringBuilder();
        while ((length = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, length, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }
}
