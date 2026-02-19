package com.example.demcayniki.config;

import java.time.Clock;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ApplicationConfiguration {
  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

//  @Bean
//  JavaMailSender mailSender() {
//    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//    mailSender.setHost("smtp.gmail.com");
//    mailSender.setPort(587);
//    mailSender.setProtocol("smtps");
//    mailSender.setDefaultEncoding("UTF-8");
//    mailSender.setUsername("admin");
//    mailSender.setPassword("admin");
//    return mailSender;
//  }
}
