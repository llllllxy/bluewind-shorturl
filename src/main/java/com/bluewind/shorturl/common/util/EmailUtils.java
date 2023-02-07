package com.bluewind.shorturl.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class EmailUtils {
    final static Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    /**
     * 发件人地址
     */
    @Value("${spring.mail.username}")
    public String sendAddress;

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送普通文本邮件
     *
     * @param address 收件人地址
     * @param title   标题
     * @param content 内容
     */
    public boolean sendTextMail(String address, String title, String content) {
        try {
            // 编辑发送邮件的一些信息，有-->发件人地址，收件人地址，邮件标题，邮件正文
            SimpleMailMessage message = new SimpleMailMessage();
            // 发件人地址
            message.setFrom(sendAddress);
            // 收件人地址，可多个，使用逗号隔开
            message.setTo(address.split(","));
            // 邮件标题
            message.setSubject(title);
            // 邮件正文
            message.setText(content);
            // private JavaMailSender mailSender;-->使用该类的send()方法发送邮件
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error("EmailUtils - sendTextMail - Exception: {e}", e);
        }
        return false;
    }


    /**
     * 发送Html邮件
     *
     * @param address 收件人地址
     * @param title   标题
     * @param text    内容
     */
    public boolean sendHtmlMail(String address, String title, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // 这里与发送文本邮件有所不同
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(sendAddress);
            helper.setTo(address.split(","));
            helper.setSubject(title);
            // 发送HTML邮件，也就是将邮件正文使用HTML的格式书写
            helper.setText(text, true);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            logger.error("EmailUtils - sendHtmlMail - Exception: {e}", e);
        }
        return false;
    }
}
