package com.velvetalon.utils;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/20 0:32 : 创建文件
 * * copy from https://blog.csdn.net/wzy18210825916/article/details/103368483
 */

import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

public class EmailUtil {
    private static String host = "smtp.qq.com";

    /**
     * 发送带附件的邮件
     *
     * @param receive      收件人
     * @param subject      邮件主题
     * @param msg          邮件内容
     * @param filePathList 课件附件的地址集合
     * @return
     * @throws GeneralSecurityException
     */
    public static void sendMail( String username, String authCode,
                                 String receive, String subject,
                                 String msg, List<String> filePathList ){
        // 获取系统属性
        Properties properties = System.getProperties();
        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");
        MailSSLSocketFactory sf;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            return;
        }
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication(){ // qq邮箱服务器账户、第三方登录授权码
                return new PasswordAuthentication(username, authCode); // 发件人邮件用户名、密码
            }
        });

        try {
            //创建MimeMessage 对象
            MimeMessage message = new MimeMessage(session);
            //设置发送人
            message.setFrom(new InternetAddress(username));
            //设置接收人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receive));
            //设置邮件主题
            message.setSubject(subject);

            // 创建普通消息部分
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(msg);

            // 创建多重消息
            Multipart multipart = new MimeMultipart();
            // 设置多重消息的文本消息部分
            multipart.addBodyPart(messageBodyPart);
            String fileName = UUIDUtil.next();
            int i = 0;
            //设置多重消息的附件部分
            for (String filePath : filePathList) {
                // 发送多个附件部分
                messageBodyPart = new MimeBodyPart();
                // 设置要发送附件的文件IO流
                DataSource source = new FileDataSource(filePath);
                String[] split = filePath.split("\\.");
                messageBodyPart.setDataHandler(new DataHandler(source));
                //设置附件名称
                messageBodyPart.setFileName(MimeUtility.encodeText(fileName + i++ + "." + split[split.length - 1]));
                multipart.addBodyPart(messageBodyPart);
            }

            //设置最终的邮件
            message.setContent(multipart);
            //发送
            Transport.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}