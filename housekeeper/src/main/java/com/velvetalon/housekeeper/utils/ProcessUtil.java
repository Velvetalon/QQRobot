package com.velvetalon.housekeeper.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/23 9:36 : 创建文件
 */
public class ProcessUtil {
    public static int GetprocessNums( String processName ){

        Runtime runtime = Runtime.getRuntime();
        List<String> tasklist = new ArrayList<>();
        java.lang.Process process;
        int count = 0; //统计进程数
        try {
            /*
             * 自适应执行查询进程列表命令
             */
            Properties prop = System.getProperties();
            //获取操作系统名称
            String os = prop.getProperty("os.name");
            if (os != null && os.toLowerCase().indexOf("linux") > -1) {
                //1.适应与linux 
                BufferedReader reader = null;
                process = runtime.exec("ps -ef");
                //显示所有进程
                process = Runtime.getRuntime().exec("ps -ef");
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(processName)) {
                        System.out.println("line===>" + line);
                        count++;
                    }
                }
            } else {
                //2.适应与windows
                process = runtime.exec("tasklist");
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String s = "";
                while ((s = br.readLine()) != null) {
                    if ("".equals(s)) {
                        continue;
                    }
                    tasklist.add(s + " ");
                }

                // 获取每列最长的长度
                String maxRow = tasklist.get(1) + "";
                String[] maxCol = maxRow.split(" ");
                // 定义映像名称数组
                String[] taskName = new String[tasklist.size()];
                // 定义 PID数组
                String[] taskPid = new String[tasklist.size()];
                // 会话名数组
                String[] taskSessionName = new String[tasklist.size()];
                // 会话#数组
                String[] taskSession = new String[tasklist.size()];
                // 内存使用 数组
                String[] taskNec = new String[tasklist.size()];
                for (int i = 0; i < tasklist.size(); i++) {
                    String data = tasklist.get(i) + "";
                    for (int j = 0; j < maxCol.length; j++) {
                        switch (j) {
                            case 0:
                                taskName[i] = data.substring(0, maxCol[j].length() + 1);
                                data = data.substring(maxCol[j].length() + 1);
                                break;
                            case 1:
                                taskPid[i] = data.substring(0, maxCol[j].length() + 1);
                                data = data.substring(maxCol[j].length() + 1);
                                break;
                            case 2:
                                taskSessionName[i] = data.substring(0, maxCol[j].length() + 1);
                                data = data.substring(maxCol[j].length() + 1);
                                break;
                            case 3:
                                taskSession[i] = data.substring(0, maxCol[j].length() + 1);
                                data = data.substring(maxCol[j].length() + 1);
                                break;
                            case 4:
                                taskNec[i] = data;
                                break;
                            default:
                                break;
                        }
                    }
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;

    }
}
