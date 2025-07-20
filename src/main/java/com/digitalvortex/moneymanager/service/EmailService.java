package com.digitalvortex.moneymanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String body) {
        try {
            log.info("Attempting to send email to: {}", to);
            log.info("From email: {}", fromEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}