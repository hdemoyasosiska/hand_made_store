package com.example.hm_store.Services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {
    @Async
    public void sendEmail(String text) {
        String username = "nikitabelov123456@mail.ru";
        String password = "pAVCsdkvD07mEghjYWfa";
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.mail.ru");
        prop.put("mail.smtp.port", "587");
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override/*w  ww  .  j a  va  2 s . c  om*/
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("nikitabelov123456@mail.ru"));
            message.setSubject("Mail Subject");
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(text, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Message sent");
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
