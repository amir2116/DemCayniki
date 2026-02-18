package com.example.demcayniki.service;

import com.example.demcayniki.model.response.LoginResponse;
import com.example.demcayniki.security.jwt.JWTService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;

  public AuthService(AuthenticationManager authenticationManager, JWTService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  public LoginResponse login(String email, String password) {

    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password)
    );

    String username = auth.getName();

    UserDetails userDetails = (auth.getPrincipal() instanceof UserDetails ud)
        ? ud
        : User.withUsername(username)
        .password("") // not used
        .authorities(auth.getAuthorities())
        .build();

    String token = jwtService.generateToken(userDetails, extractClaims(auth));

    return new LoginResponse(token, "Bearer", username);
  }

  private Map<String, Object> extractClaims(Authentication auth) {
    List<String> roles = auth.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .filter(Objects::nonNull)
        .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
        .distinct()
        .toList();

    return Map.of("roles", roles);
  }
}
