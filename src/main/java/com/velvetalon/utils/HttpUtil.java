package com.velvetalon.utils;

import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/10 14:55 : 创建文件
 */
public class HttpUtil {

    private static HttpClient httpClient;

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    static{
        httpClient = HttpClientBuilder.create().build();
    }

    public static String download( String urlString,HttpHost httpProxy,Map<String,String> header, String filename, String savePath ) throws Exception{
        HttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(urlString);
        if (httpProxy != null) {
            RequestConfig requestConfig = RequestConfig.custom().setProxy(httpProxy).setConnectTimeout(30000).setSocketTimeout(3000).setConnectionRequestTimeout(3000).build();
            httpGet.setConfig(requestConfig);
        }
        if (header != null && !header.isEmpty()) {
            for (String k : header.keySet()) {
                httpGet.setHeader(k, header.get(k));
            }
        }

        HttpResponse response = client.execute(httpGet);

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();


        File file = new File(savePath+"/"+filename);
        file.getParentFile().mkdirs();
        FileOutputStream fileout = new FileOutputStream(file);
        /**
         * 根据实际运行效果 设置缓冲区大小
         */
        byte[] buffer = new byte[4096];
        int ch = 0;
        while ((ch = is.read(buffer)) != -1) {
            fileout.write(buffer, 0, ch);
        }
        is.close();
        fileout.flush();
        fileout.close();


        return file.getAbsolutePath();
    }

    @SneakyThrows
    public static void get( String url, Map<String, String> queryString,
                            Map<String, String> header,
                            HttpHost httpProxy,
                            ResponseHandler handler ){

        if (!url.endsWith("?")) {
            url += "?";
        }
        List<String> list = new ArrayList<>();
        if (queryString != null && !queryString.isEmpty()) {
            for (String k : queryString.keySet()) {
                list.add(k + "=" + queryString.get(k));
            }
        }

        url += String.join("&", list);

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
        URL urlparse = new URL(url);
        httpGet.setHeader("Host", urlparse.getHost());

        if (header != null && !header.isEmpty()) {
            for (String k : header.keySet()) {
                httpGet.setHeader(k, header.get(k));
            }
        }
        if (httpProxy != null) {
            RequestConfig requestConfig = RequestConfig.custom().setProxy(httpProxy).setConnectTimeout(30000).setSocketTimeout(30000).setConnectionRequestTimeout(30000).build();
            httpGet.setConfig(requestConfig);
            logger.info("已设置代理");
        }
        httpClient.execute(httpGet, handler);
    }
}
