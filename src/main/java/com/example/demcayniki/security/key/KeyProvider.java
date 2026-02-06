package com.example.demcayniki.security.key;

import java.security.PrivateKey;
import java.security.PublicKey;
import org.springframework.stereotype.Component;

@Component
public interface KeyProvider {

  PrivateKey getPrivateKey();
  PublicKey getPublicKey();
}
