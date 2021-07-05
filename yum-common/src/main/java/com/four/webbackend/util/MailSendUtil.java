package com.four.webbackend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lbavsc
 * @version 1.0
 * @className MailSendUtil
 * @description
 * @date 2021/7/5 下午3:19
 **/
@Component
public class MailSendUtil {

    private JavaMailSenderImpl mailSender;

    @Autowired
    public void setMailSender(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }


    @Value("${spring.mail.username}")
    private String userName;

    private final String user = "云盘系统";

    /**
     * 注册验证码
     *
     * @param toEmail 接收者邮箱
     * @param code    验证码
     */
    public void mailSendCheckCode(String toEmail, String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setSubject("注册验证码");
        String count = "注册验证码: " + code + "，5分钟内有效\n";
        simpleMailMessage.setText(count);
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setFrom(user + '<' + userName + '>');
        mailSender.send(simpleMailMessage);
    }

    /**
     * 找回密码验证码
     *
     * @param toEmail 接收者邮箱
     * @param code    验证码
     */
    public void mailSendCheckCodeResetPasswd(String toEmail, String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("找回密码验证码");
        String count = "找回密码验证码: " + code + "，5分钟内有效\n";
        simpleMailMessage.setText(count);
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setFrom(user + '<' + userName + '>');
        mailSender.send(simpleMailMessage);
    }

    /**
     * 修改信息验证码
     *
     * @param toEmail 接收者邮箱
     * @param code    验证码
     */
    public void mailSendChangeInfoCheckCode(String toEmail, String code) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("信息修改验证码");
        String count = "信息修改验证码: " + code + "，5分钟内有效\n";
        simpleMailMessage.setText(count);
        simpleMailMessage.setTo(toEmail);
        simpleMailMessage.setFrom(user + '<' + userName + '>');
        mailSender.send(simpleMailMessage);
    }
}
