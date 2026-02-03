package com.example.demcayniki.service;


import com.example.demcayniki.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

  private final JwtProperties jwtProperties;
  private final SecretKey key;

  private final Clock clock;

  public JWTService(JwtProperties jwtProperties, Clock clock) {
    this.jwtProperties = Objects.requireNonNull(jwtProperties, "JwtProperties must not be null");
    this.clock = Objects.requireNonNull(clock, "Clock must not be null");

    String secret = Objects.requireNonNull(jwtProperties.secret(), "JWT secret must be set");
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public String generateToken(UserDetails userDetails, Map<String, Object> claims) {
    Instant now = Instant.now(clock);
    Instant exp = now.plus(jwtProperties.expirationMinutes(), MINUTES);

    return Jwts.builder()
        .issuer(jwtProperties.issuer())
        .subject(userDetails.getUsername())
        .claims(claims)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(key)
        .compact();
  }

  public String extractUsername(String token) {
    return parse(token).getPayload().getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    Claims claims = parse(token).getPayload();
    Instant now = Instant.now(clock);

    return Objects.equals(claims.getSubject(), userDetails.getUsername())
        && claims.getExpiration().after(Date.from(now));
  }

  private Jws<Claims> parse(String token) {
    return Jwts.parser()
        .requireIssuer(jwtProperties.issuer())
        .verifyWith(key)
        .build()
        .parseSignedClaims(token);
  }
}
