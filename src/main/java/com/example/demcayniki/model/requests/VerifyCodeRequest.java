package com.example.demcayniki.model.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyCodeRequest {

  @NotBlank
  @NotNull
  private String UUid;

  @NotBlank
  @NotNull
  private String code;
}

