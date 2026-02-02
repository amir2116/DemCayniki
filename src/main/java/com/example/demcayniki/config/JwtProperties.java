package com.example.demcayniki.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
record JwtProperties(
    String secret,
    long expirationMinutes,
    String issuer
) {

}
