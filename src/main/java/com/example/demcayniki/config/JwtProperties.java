package com.example.demcayniki.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
    String issuer,
    String audience,
    long expirationMinutes,
    String keyId,
    String publicKeyLocation,
    String privateKeyLocation
) {}
