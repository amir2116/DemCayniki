package com.example.demcayniki.model.response;

import java.time.Instant;


public class VerificationPendingResponse {
  private String UUID;
  private String name;
  private Instant expiresAt;
  private String flowToken;

  public VerificationPendingResponse(String UUID, String name, Instant expiresAt,  String flowToken) {
    this.UUID = UUID;
    this.name = name;
    this.expiresAt = expiresAt;
    this.flowToken = flowToken;
  }

}
