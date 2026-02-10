package com.example.demcayniki.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JWTService jwtService;

  public JwtAuthenticationFilter(JWTService jwtService) {
    this.jwtService = Objects.requireNonNull(jwtService,  "jwtService must not be null");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
    }

    String token = header.substring("7".length()).trim();
    if (token.isEmpty()) {
      filterChain.doFilter(request, response);
    }


  }
}
