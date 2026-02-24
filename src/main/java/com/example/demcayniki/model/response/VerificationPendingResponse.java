package com.example.demcayniki.model.response;

import java.time.Instant;


public class VerificationPendingResponse {
  private String UUID;
  private String name;
  private Instant expiresAt;

  public VerificationPendingResponse(String UUID, String name, Instant expiresAt) {
    this.UUID = UUID;
    this.name = name;
    this.expiresAt = expiresAt;
  }

}
