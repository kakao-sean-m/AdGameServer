package com.fufumasi.AdGameServer.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class EmailHandler {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendMail(String addr) throws MessagingException {
        if (javaMailSender == null) {
            System.out.println("javaMailSender NULL");
            return;
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        message.setSubject("hi");
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(addr));
        message.setText("test");
        message.setSentDate(new Date());
        javaMailSender.send(message);
    }
}
