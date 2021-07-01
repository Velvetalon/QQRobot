package com.velvetalon.utils;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * @describe: Md5工具类
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/30 16:04 : 创建文件
 * * copy from https://www.cnblogs.com/loong-hon/p/10237075.html
 */
public class Md5Util {
    public static String getMd5( InputStream is, boolean close ){
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (is != null && close) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMd5( InputStream is ){
        return getMd5(is, true);
    }
}
