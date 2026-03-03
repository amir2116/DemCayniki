package com.example.demcayniki.service;

import static com.example.demcayniki.model.constants.modifiable.RoleEnum.ROLE_PENDING_USER;
import static com.example.demcayniki.model.constants.modifiable.UserStatus.ACTIVE;
import static com.example.demcayniki.model.constants.modifiable.UserStatus.PENDING;
import static com.example.demcayniki.model.constants.modifiable.UserStatus.REMOVED;
import static com.example.demcayniki.util.SecurityUtils.secureEquals;

import com.example.demcayniki.domain.entity.ConsumerUser;
import com.example.demcayniki.domain.secondary.PendingRegistration;
import com.example.demcayniki.model.requests.RegisterRequest;
import com.example.demcayniki.model.requests.VerifyCodeRequest;
import com.example.demcayniki.model.response.LoginResponse;
import com.example.demcayniki.model.response.VerificationPendingResponse;
import com.example.demcayniki.repository.ConsumerUserRepository;
import com.example.demcayniki.repository.PendingRegistrationRepository;
import com.example.demcayniki.security.jwt.JWTService;
import com.example.demcayniki.util.HmacHasher;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private static final String TOKEN_TYPE = "Bearer";

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JWTService jwtService;
  private final ConsumerUserRepository consumerUserRepository;
  private final HmacHasher hmacHasher;
  private final MailService mailService;
  private final PendingRegistrationRepository pendingRegistrationRepository;
  private final PasswordEncoder passwordEncoder;


  public AuthService(AuthenticationManager authenticationManager, JWTService jwtService,
                     ConsumerUserRepository consumerUserRepository, HmacHasher hmacHasher, MailService mailService,
                     PendingRegistrationRepository pendingRegistrationRepository,
                     UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.consumerUserRepository = consumerUserRepository;
    this.hmacHasher = hmacHasher;
    this.mailService = mailService;
    this.pendingRegistrationRepository = pendingRegistrationRepository;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  public LoginResponse login(String email, String password) {

    if (email == null || password == null) {
      throw new RuntimeException("invalid credentials");
    }

    String normalizedEmail = email.trim().toLowerCase();

    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(normalizedEmail, password)
    );

    if (!(auth.getPrincipal() instanceof UserDetails userDetails)) {
      throw new IllegalStateException("Authentication principal is not UserDetails");
    }

    String token = jwtService.generateToken(userDetails, extractClaims(auth));

    return new LoginResponse(token, TOKEN_TYPE, userDetails.getUsername());
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

  @Transactional(rollbackOn = Exception.class)
  public VerificationPendingResponse register(RegisterRequest registerRequest) {
    String email = registerRequest.getEmail().trim();
    if (consumerUserRepository.existsByEmailAndStatusNot(email, REMOVED.getCode())) {
      throw new RuntimeException("email is already in use");
    }
    Instant now = Instant.now();
    PendingRegistration pendingRegistration = pendingRegistrationRepository.findByEmail(email).orElse(null);
    if (pendingRegistration!= null ) {
      if(pendingRegistration.isVerified()) {
        throw new RuntimeException("email is already in use");
      }
      if (pendingRegistration.getExpiresAt().isAfter(now)) {
        String flowToken = generateFlowToken();
        pendingRegistration.setFlowTokenHash(hmacHasher.hmacSha256Hex(flowToken));
        pendingRegistrationRepository.save(pendingRegistration);
      return new VerificationPendingResponse(String.valueOf(pendingRegistration.getId()),
          registerRequest.getName(),
          pendingRegistration.getExpiresAt(),
          flowToken
          );
      }
    }


    String verificationCode = String.valueOf(SECURE_RANDOM.nextInt(100_000, 1_000_000));
    String flowToken = generateFlowToken();

    pendingRegistration = savePending(email, verificationCode, flowToken);

    saveConsumerUser(email, registerRequest);

    mailService.sendEmail(email, registerRequest.getName(), verificationCode);


    return new VerificationPendingResponse(String.valueOf(pendingRegistration.getId()),
        registerRequest.getName(),
        pendingRegistration.getExpiresAt(),
        flowToken

        );
  }

  @Transactional(rollbackOn = Exception.class)
  public LoginResponse verify(VerifyCodeRequest req) {
    PendingRegistration p = pendingRegistrationRepository
        .findById(UUID.fromString(req.getUUID()))
        .orElseThrow(() -> new RuntimeException("invalid verification code"));

    Instant now = Instant.now();

    if (p.getExpiresAt().isBefore(now) || p.getFlowExpiresAt().isBefore(now)) {
      throw new RuntimeException("verification expired, please request a new code");
    }

    if (p.getAttemptsLeft() <= 0) {
      throw new RuntimeException("too many attempts, please request a new code");
    }

    String codeHash = hmacHasher.hmacSha256Hex(req.getCode());
    String flowHash = hmacHasher.hmacSha256Hex(req.getFlowToken());

    boolean flowOk = secureEquals(p.getFlowTokenHash(), flowHash);
    boolean codeOk = secureEquals(p.getCodeHash(), codeHash);

    if (!flowOk || !codeOk) {
      p.setAttemptsLeft(p.getAttemptsLeft() - 1);
      pendingRegistrationRepository.save(p);
      throw new RuntimeException("invalid verification code");
    }

    List<ConsumerUser> users =
        consumerUserRepository.findAllByEmailAndStatus(p.getEmail(), PENDING.getCode());

    if (users.isEmpty()) {
      throw new RuntimeException("pending user not found");
    }
    if (users.size() > 1) {
      throw new IllegalStateException("data corruption: multiple pending users");
    }

    ConsumerUser user = users.getFirst();
    user.setStatus(ACTIVE.getCode());
    consumerUserRepository.save(user);

    // success: delete pending
    pendingRegistrationRepository.delete(p);

    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String token = jwtService.generateToken(
        userDetails,
        Map.of("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
            .distinct()
            .toList()
        )
    );

    return new LoginResponse(token, "Bearer", userDetails.getUsername());
  }

  private PendingRegistration savePending(String email, String verificationCode, String flowToken) {
    Instant now = Instant.now();
    PendingRegistration pendingRegistration = PendingRegistration.builder()
        .id(UUID.randomUUID())
        .email(email)
        .attemptsLeft(10)
        .codeHash(hmacHasher.hmacSha256Hex(verificationCode))
        .verified(false)
        .createdAt(now)
        .expiresAt(now.plusSeconds(300))
        .flowTokenHash(hmacHasher.hmacSha256Hex(flowToken))
        .flowExpiresAt(now.plusSeconds(300))
        .build();
    return pendingRegistrationRepository.save(pendingRegistration);
  }

  private void saveConsumerUser(String email, RegisterRequest registerRequest) {
    ConsumerUser user = ConsumerUser.builder()
        .id(UUID.randomUUID())
        .email(email)
        .firstName(registerRequest.getName())
        .lastName(registerRequest.getLastName())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .build();
    user.grantRole(ROLE_PENDING_USER);
    consumerUserRepository.save(user);
  }

  private String generateFlowToken() {
    byte[] randomBytes = new byte[32];
    SECURE_RANDOM.nextBytes(randomBytes);
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(randomBytes);
  }
}
