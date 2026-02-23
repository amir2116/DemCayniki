package com.example.demcayniki.service;

import static com.example.demcayniki.model.constants.modifiable.UserStatus.REMOVED;

import com.example.demcayniki.domain.entity.ConsumerUser;
import com.example.demcayniki.domain.secondary.PendingRegistration;
import com.example.demcayniki.model.requests.RegisterRequest;
import com.example.demcayniki.model.response.LoginResponse;
import com.example.demcayniki.repository.ConsumerUserRepository;
import com.example.demcayniki.security.jwt.JWTService;
import com.example.demcayniki.util.HmacHasher;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JWTService jwtService;
  private final ConsumerUserRepository consumerUserRepository;
  private final HmacHasher  hmacHasher;

  private final Random random=new Random();

  public AuthService(AuthenticationManager authenticationManager, JWTService jwtService, ConsumerUserRepository consumerUserRepository, HmacHasher hmacHasher) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.consumerUserRepository = consumerUserRepository;
    this.hmacHasher = hmacHasher;
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

  public LoginResponse register(RegisterRequest registerRequest) {
    List<ConsumerUser> userList = consumerUserRepository.findAllByEmail(registerRequest.getEmail());

    if(userList.stream().anyMatch(user -> REMOVED.getCode()!=user.getStatus())) {
      throw new RuntimeException("gmail is already in use");
    }
    Instant now = Instant.now();
    String verificationCode = String.valueOf(random.nextInt(100_000,1_000_000));
    PendingRegistration pendingRegistration = PendingRegistration.builder()
        .email(registerRequest.getEmail())
        .attemptsLeft(50)
        .createdAt(now)
        .expiresAt(now.plusSeconds(300))
        .codeHash(hmacHasher.hmacSha256Hex(verificationCode))
        .build();
return null;
  }


}
