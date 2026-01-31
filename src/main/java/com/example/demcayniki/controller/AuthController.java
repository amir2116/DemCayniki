package com.example.demcayniki.controller;

import com.example.demcayniki.model.requests.AuthRequest;
import com.example.demcayniki.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest authRequest) {
        Authentication auth = authService.authenticate(authRequest.getEmail(), authRequest.getPassword());
        return ResponseEntity.ok("LOGIN OK: " + auth.getName());
    }

}
