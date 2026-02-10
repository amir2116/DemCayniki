package com.example.demcayniki.security.jwt;


import com.example.demcayniki.config.JwtProperties;
import com.example.demcayniki.security.key.KeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Clock;
import java.time.Instant;
import static java.time.temporal.ChronoUnit.MINUTES;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

  private final JwtProperties jwtProperties;
  private final PrivateKey privateKey;
  private final PublicKey publicKey;

  private final Clock clock;

  public JWTService(JwtProperties jwtProperties, KeyProvider keyProvider, Clock clock) {
    this.jwtProperties = Objects.requireNonNull(jwtProperties, "JwtProperties must not be null");
    this.privateKey=keyProvider.getPrivateKey();
    this.publicKey=keyProvider.getPublicKey();
    this.clock = Objects.requireNonNull(clock, "Clock must not be null");
  }

  public String generateToken(UserDetails userDetails, Map<String, Object> claims) {
    Instant now = Instant.now(clock);
    Instant exp = now.plus(jwtProperties.expirationMinutes(), MINUTES);

    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .issuer(jwtProperties.issuer())
        .audience()
            .add(jwtProperties.audience()).and()
        .subject(userDetails.getUsername())
        .claims(claims)
        .issuedAt(Date.from(now))
        .expiration(Date.from(exp))
        .signWith(privateKey, Jwts.SIG.RS256)
        .compact();
  }

  public Claims parseAndValidate(String token) {
    return Jwts.parser()
        .verifyWith(publicKey)
        .requireIssuer(jwtProperties.issuer())
        .requireAudience(jwtProperties.audience())
        .clockSkewSeconds(60)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
