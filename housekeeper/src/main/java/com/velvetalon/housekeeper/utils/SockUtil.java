package com.velvetalon.housekeeper.utils;

import java.io.*;
import java.net.Socket;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/23 14:56 : 创建文件
 */
public class SockUtil {
    public static void sendTcpCommand( String ip, Integer port, String command ) throws IOException{
        Socket socket = new Socket(ip, port);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write(command + "\n");
        bw.close();
        socket.close();
    }
}
