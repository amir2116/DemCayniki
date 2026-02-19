package com.example.demcayniki.service;

import com.example.demcayniki.config.MailProperties;
import java.util.Objects;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
@Service
public class MailService {

  private final JavaMailSender mailSender;
  private final String from;

  public MailService(JavaMailSender mailSender, MailProperties mailProperties) {
    this.mailSender = Objects.requireNonNull(mailSender);
    this.from = Objects.requireNonNull(mailProperties.from());
  }

  @Async
  public void sendEmail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    mailSender.send(message);
  }
}
