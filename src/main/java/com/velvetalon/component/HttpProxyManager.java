package com.velvetalon.component;

import org.apache.http.HttpHost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 15:39 : 创建文件
 */
@Component
public class HttpProxyManager {
    @Value("${http-proxy.host:null}")
    private String host;

    @Value("${http-proxy.port:0}")
    private Integer port;

    @Value("${http-proxy.protocol:null}")
    private String protocol;

    @Value("${http-proxy.enable:null}")
    private boolean enable;

    public HttpHost getHttpProxy(){
        return enable ? new HttpHost(host,port,protocol) : null;
    }

    public boolean isEnable(){
        return enable;
    }
}
