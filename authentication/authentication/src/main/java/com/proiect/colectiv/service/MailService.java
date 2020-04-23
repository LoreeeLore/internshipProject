package com.proiect.colectiv.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Async
public class MailService {
    private static final Logger LOG = Logger.getLogger(MailService.class);


    @Autowired
    private JavaMailSender mailSender;


    public void sendEmail(String email, String pass) {

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);

        msg.setSubject("Password Change Request");
        msg.setText("Your new password is        " + pass);

        mailSender.send(msg);

    }


}

