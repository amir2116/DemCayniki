package com.example.demcayniki.util;

import com.example.demcayniki.config.properties.VerificationProperties;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class HmacHasher {

  private final byte[] secret;

  public HmacHasher(VerificationProperties props) {
    String s = props.hmacSecret();
    if (s == null || s.isBlank()) {
      throw new IllegalStateException("app.verification.hmac-secret is missing/blank");
    }
    this.secret = s.getBytes(StandardCharsets.UTF_8);
  }

  public String hmacSha256Hex(String value) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret, "HmacSHA256"));

      byte[] out = mac.doFinal(value.getBytes(StandardCharsets.UTF_8));

      StringBuilder sb = new StringBuilder(out.length * 2);
      for (byte b : out) sb.append(String.format("%02x", b));
      return sb.toString();

    } catch (Exception e) {
      throw new IllegalStateException("HMAC init failed", e);
    }
  }

  boolean matchesHmacSha256Hex(String rawValue, String storedHex) {
    String calc = hmacSha256Hex(rawValue);
    return MessageDigest.isEqual(
        calc.getBytes(StandardCharsets.UTF_8),
        storedHex.getBytes(StandardCharsets.UTF_8)
    );
  }
}