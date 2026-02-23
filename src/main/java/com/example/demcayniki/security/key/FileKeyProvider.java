package com.example.demcayniki.security.key;

import com.example.demcayniki.config.properties.JwtProperties;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class FileKeyProvider implements KeyProvider {

  private final PrivateKey privateKey;
  private final PublicKey publicKey;

  public FileKeyProvider(JwtProperties jwtProperties, ResourceLoader resourceLoader) {
    Objects.requireNonNull(jwtProperties, "JwtProperties must not be null");

    this.privateKey = loadPrivateKey(resourceLoader.getResource(jwtProperties.privateKeyLocation()));
    this.publicKey = loadPublicKey(resourceLoader.getResource(jwtProperties.publicKeyLocation()));
  }

  @Override
  public PrivateKey getPrivateKey() {
    return privateKey;
  }

  @Override
  public PublicKey getPublicKey() {
    return publicKey;
  }

  private PrivateKey loadPrivateKey(Resource resource) {
    try (InputStream in = resource.getInputStream()) {
      String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      String base64 = pem
          .replace("-----BEGIN PRIVATE KEY-----", "")
          .replace("-----END PRIVATE KEY-----", "")
          .replaceAll("\\s", "");

      byte[] der = Base64.getDecoder().decode(base64);
      PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(der);

      // RSA for RS256. If you use ES256, use "EC".
      return KeyFactory.getInstance("RSA").generatePrivate(spec);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load private key: " + safeName(resource), e);
    }
  }

  private PublicKey loadPublicKey(Resource resource) {
    try (InputStream in = resource.getInputStream()) {
      String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      String base64 = pem
          .replace("-----BEGIN PUBLIC KEY-----", "")
          .replace("-----END PUBLIC KEY-----", "")
          .replaceAll("\\s", "");

      byte[] der = Base64.getDecoder().decode(base64);
      X509EncodedKeySpec spec = new X509EncodedKeySpec(der);

      // RSA for RS256. If you use ES256, use "EC".
      return KeyFactory.getInstance("RSA").generatePublic(spec);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to load public key: " + safeName(resource), e);
    }
  }

  private String safeName(Resource resource) {
    try {
      return resource.getURI().toString();
    } catch (Exception e) {
      return resource.toString();
    }
  }

}
