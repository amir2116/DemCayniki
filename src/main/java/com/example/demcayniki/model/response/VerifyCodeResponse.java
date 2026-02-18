package com.example.demcayniki.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerifyCodeResponse {
  private final String registrationToken;
}
