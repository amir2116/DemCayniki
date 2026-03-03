package com.example.demcayniki.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class SecurityUtils {

  private SecurityUtils() {
    // prevent instantiation
  }

  public static boolean secureEquals(String a, String b) {
    if (a == null || b == null) {
      return false;
    }

    return MessageDigest.isEqual(
        a.getBytes(StandardCharsets.UTF_8),
        b.getBytes(StandardCharsets.UTF_8)
    );
  }


}
