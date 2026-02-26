package com.example.demcayniki.service;

import static com.example.demcayniki.model.constants.modifiable.UserStatus.REMOVED;

import com.example.demcayniki.domain.entity.ConsumerUser;
import com.example.demcayniki.domain.entity.Roles;
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
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import lombok.Synchronized;
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
  private final ConsumerUserRepository consumerUserRepository;
  private final HmacHasher  hmacHasher;
  private final MailService mailService;
  private final PendingRegistrationRepository pendingRegistrationRepository;

  private final Random random=new Random();

  public AuthService(AuthenticationManager authenticationManager, JWTService jwtService,
                     ConsumerUserRepository consumerUserRepository, HmacHasher hmacHasher, MailService mailService,
                     PendingRegistrationRepository pendingRegistrationRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.consumerUserRepository = consumerUserRepository;
    this.hmacHasher = hmacHasher;
    this.mailService = mailService;
    this.pendingRegistrationRepository = pendingRegistrationRepository;
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

  @Transactional(rollbackOn =  Exception.class)
  @Synchronized
  public VerificationPendingResponse register(RegisterRequest registerRequest) {
    List<ConsumerUser> userList = consumerUserRepository.findAllByEmail(registerRequest.getEmail());

    if(userList.stream().anyMatch(user -> REMOVED.getCode()!=user.getStatus())) {
      throw new RuntimeException("email is already in use");
    }
    List<PendingRegistration> pendings =pendingRegistrationRepository.findAllByEmail(registerRequest.getEmail());
    if(pendings.stream().anyMatch(PendingRegistration::isVerified)){
      throw new RuntimeException("email is already in use");
    }
    if(pendings.stream().anyMatch(pendingRegistration -> pendingRegistration.getExpiresAt().isBefore(Instant.now()))){
      PendingRegistration pendingRegistration = pendingRegistrationRepository.findTopByEmailOrderByCreatedAtDesc(registerRequest.getEmail()).orElseThrow(()->new RuntimeException("pendingRegistration not found"));
      return new VerificationPendingResponse(String.valueOf(pendingRegistration.getId()),registerRequest.getName(), pendingRegistration.getExpiresAt());
    }


    String verificationCode = String.valueOf(random.nextInt(100_000,1_000_000));

    PendingRegistration pendingRegistration = savePending(registerRequest.getEmail(), verificationCode);

    saveConsumerUser(registerRequest);

    mailService.sendEmail(registerRequest.getEmail(), registerRequest.getName(), verificationCode);

    return new VerificationPendingResponse(String.valueOf(pendingRegistration.getId()), registerRequest.getName(), pendingRegistration.getExpiresAt());
  }



  public VerificationPendingResponse verify(VerifyCodeRequest verifyCodeRequest) {
return null;
  }

  private PendingRegistration savePending(String email, String verificationCode) {
    Instant now = Instant.now();
    PendingRegistration pendingRegistration = PendingRegistration.builder()
        .id(UUID.randomUUID())
        .email(email)
        .attemptsLeft(50)
        .codeHash(hmacHasher.hmacSha256Hex(verificationCode))
        .verified(false)
        .createdAt(now)
        .expiresAt(now.plusSeconds(300))
        .build();
    return pendingRegistrationRepository.save(pendingRegistration);
  }

  private void saveConsumerUser(RegisterRequest registerRequest) {
    ConsumerUser user = ConsumerUser.builder()
        .id(UUID.randomUUID())
        .email(registerRequest.getEmail())
        .password(hmacHasher.hmacSha256Hex(registerRequest.getPassword()))
        .build();
    HashSet<Roles> roles = new HashSet<>();
  }

}
