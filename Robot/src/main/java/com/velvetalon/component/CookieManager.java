package com.velvetalon.component;

import lombok.SneakyThrows;
import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @describe: cookie管理器
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 11:31 : 创建文件
 */
@Component
public class CookieManager {
    private Map<String, String> cookieMap;

    public CookieManager(){}

    @Value("${cookie-file}")
    private String cookieFile;

    @PostConstruct
    public void init(){
        this.cookieMap = new HashMap<>();
        if (new File(cookieFile).exists()){
            updateAll(readFile(new File(cookieFile)));
        }
    }


    @Override
    public String toString(){
        List<String> list = new ArrayList<>();
        for (String k : cookieMap.keySet()) {
            list.add(k + "=" + cookieMap.get(k));
        }
        return String.join(";", list);
    }

    public Map<String, String> getCookieMap(){
        String cookie = toString();
        Map<String, String> map = new HashMap<>();
        map.put("cookie", cookie);
        return map;
    }

    private void update( String k, String v ){
        cookieMap.put(k, v);
    }

    public void updateByHeaders( Header[] headers ){
        for (Header header : headers) {
            updateAll(header.getValue());
        }
        syncFile();
    }

    private void updateAll( String cookies ){
        String[] split = cookies.split(";");
        for (String cookie : split) {
            String[] kv = cookie.split("=");
            if (kv.length == 2) {
                update(kv[0], kv[1]);
            }
        }
    }

    @SneakyThrows
    private static String readFile( File file ){
        assert file.isFile() && file.exists();
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[4096];
        int length = 0;
        StringBuilder sb = new StringBuilder();
        while ((length = fis.read(buf)) != -1) {
            sb.append(new String(buf, 0, length, StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    @SneakyThrows
    private synchronized void syncFile(){
        CookieManager _this = this;
        AsyncWrapper.submit(new Runnable() {
            @Override
            @SneakyThrows
            public void run(){
                File file = new File(cookieFile);
                if (file.exists()) {
                    file.delete();
                    file.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(file);
                FileChannel channel = fos.getChannel();
                ByteBuffer src = StandardCharsets.UTF_8.encode(_this.toString());
                while (channel.write(src) != 0) {
                }
            }
        });
    }
}
