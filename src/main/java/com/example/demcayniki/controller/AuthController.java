package com.example.demcayniki.controller;

import com.example.demcayniki.model.requests.AuthRequest;
import com.example.demcayniki.model.requests.RegisterRequest;
import com.example.demcayniki.model.requests.VerifyCodeRequest;
import com.example.demcayniki.model.response.LoginResponse;
import com.example.demcayniki.model.response.VerificationPendingResponse;
import com.example.demcayniki.service.AuthService;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {


    private final AuthService authService;

  public AuthController(AuthService authService) {
        this.authService = authService;
    }

  @PostMapping("/login")
  public ResponseEntity<@NonNull LoginResponse> login(@Valid @RequestBody AuthRequest authRequest) {
    return ResponseEntity.ok(
        authService.login(authRequest.getEmail(), authRequest.getPassword())
    );
  }

  @PostMapping("/register")
  public ResponseEntity<VerificationPendingResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
      return ResponseEntity.ok(
          authService.register(registerRequest)
      );
  }

  @PostMapping("/verify")
  public ResponseEntity<VerificationPendingResponse> verify(@Valid @RequestBody VerifyCodeRequest verifyCodeRequest) {
    return ResponseEntity.ok(
        authService.verify(verifyCodeRequest)
    );
  }



}
