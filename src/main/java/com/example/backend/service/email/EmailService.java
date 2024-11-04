package com.example.backend.service.email;

import java.util.Map;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendEmailWithTemplate(String to, String subject, String templateName,
            Map<String, Object> templateModel) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            Context context = new Context();
            context.setVariables(templateModel);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException("Failed to send email: ", e);
        } catch (MessagingException e) {
            throw new RuntimeException("Error sending email", e);
        }
    }
}
