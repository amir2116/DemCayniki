package com.example.demcayniki.service;

import com.example.demcayniki.config.properties.MailProperties;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
class MailService {

  private static final Logger log = LoggerFactory.getLogger(MailService.class);

  private final JavaMailSender mailSender;
  private final String from;

  MailService(JavaMailSender mailSender, MailProperties mailProperties) {
    this.mailSender = Objects.requireNonNull(mailSender);
    this.from = Objects.requireNonNull(mailProperties.from());
  }

  @Async
  void sendEmail(String to, String subject, String text) {
    if (to == null || to.isBlank()) {
      throw new IllegalArgumentException("'to' must not be blank");
    }
    if (subject == null) subject = "";
    if (text == null) text = "";

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);

    try {
      mailSender.send(message);
      log.info("Email sent to='{}' subject='{}'", to, subject);
    } catch (MailException ex) {
      log.error("Email failed to='{}' subject='{}' error='{}'", to, subject, ex.getMessage(), ex);
      throw ex;
    }
  }
}