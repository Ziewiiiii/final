/**
 * Coded by 余卓炜 202330452181
 */
package com.myyzw.myweb2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 邮件服务类
 */
@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@myweb2.com}")
    private String fromEmail;

    /**
     * 发送订单确认邮件（异步执行，不阻塞主流程）
     * @param toEmail 收件人邮箱
     * @param orderId 订单ID
     * @param totalPrice 订单总价
     */
    @Async
    public void sendOrderConfirmationEmail(String toEmail, Long orderId, String totalPrice) {
        if (mailSender == null) {
            // 如果没有配置邮件发送器，则跳过发送（开发环境可能没有配置）
            System.out.println("邮件发送器未配置，跳过邮件发送。订单ID: " + orderId + ", 收件人: " + toEmail);
            return;
        }

        if (toEmail == null || toEmail.trim().isEmpty()) {
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail); // 发件人邮箱（从配置文件读取，默认使用spring.mail.username）
            message.setTo(toEmail);
            message.setSubject("订单确认 - 订单号: " + orderId);
            message.setText("尊敬的客户，\n\n" +
                    "您的订单已成功提交！\n\n" +
                    "订单号：" + orderId + "\n" +
                    "订单金额：¥" + totalPrice + "\n\n" +
                    "感谢您的购买！\n\n" +
                    "此邮件由系统自动发送，请勿回复。");
            
            mailSender.send(message);
            System.out.println("订单确认邮件已发送到: " + toEmail);
        } catch (Exception e) {
            System.err.println("发送邮件失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

