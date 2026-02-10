package com.example.demcayniki.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final JWTService jwtService;

  JwtAuthenticationFilter(JWTService jwtService) {
    this.jwtService = Objects.requireNonNull(jwtService, "JWTService must not be null");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return;
    }

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || header.isBlank() || !startsWithBearerIgnoreCase(header)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(BEARER_PREFIX.length()).trim();
    if (token.isEmpty()) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      Claims claims = jwtService.parseAndValidate(token);

      String userId = claims.getSubject();
      if (userId == null || userId.isBlank()) {
        throw new JwtException("Missing sub");
      }

      Collection<? extends GrantedAuthority> authorities = extractAuthorities(claims);

      var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
      auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(auth);

      filterChain.doFilter(request, response);

    } catch (JwtException | IllegalArgumentException ex) {
      SecurityContextHolder.clearContext();
      // Let AuthenticationEntryPoint handle the response (401 JSON)
      throw ex;
    }
  }

  private boolean startsWithBearerIgnoreCase(String header) {
    return header.length() >= BEARER_PREFIX.length()
        && header.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length());
  }

  private List<GrantedAuthority> extractAuthorities(Claims claims) {
    Object roles = claims.get("roles");
    if (roles instanceof List<?> list) {
      return list.stream()
          .filter(Objects::nonNull)
          .map(String::valueOf)
          .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }

    Object scopes = claims.get("scopes");
    if (scopes instanceof List<?> list) {
      return list.stream()
          .filter(Objects::nonNull)
          .map(String::valueOf)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }

    Object scope = claims.get("scope");
    if (scope instanceof String s && !s.isBlank()) {
      return Stream.of(s.split("\\s+"))
          .filter(x -> !x.isBlank())
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }

    return List.of();
  }
}
