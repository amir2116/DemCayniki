package com.example.demcayniki.domain.secondary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "PENDING_REGISTRATIONS",
    uniqueConstraints = @UniqueConstraint(name = "uk_pending_reg_email", columnNames = "EMAIL"),
    indexes = @Index(name = "idx_pending_reg_email", columnList = "EMAIL")
)
public class PendingRegistration {

  @Id
  @UuidGenerator
  @Column(name = "ID", nullable = false, updatable = false)
  UUID id;

  @Column(name = "EMAIL", nullable = false, length = 320)
  String email;

  @Column(name = "CODE_HASH", nullable = false, length = 64)
  String codeHash;

  @Column(name = "FLOW_TOKEN_HASH", nullable = false, length = 64)
  String flowTokenHash;

  @Column(name = "FLOW_EXPIRES_AT", nullable = false)
  Instant flowExpiresAt;

  @Column(name = "VERIFIED", nullable = false)
  boolean verified;

  @Column(name = "ATTEMPTS_LEFT", nullable = false)
  int attemptsLeft;

  @Column(name = "CREATED_AT", nullable = false)
  Instant createdAt;

  @Column(name = "EXPIRES_AT", nullable = false)
  Instant expiresAt;

  @Column(name = "RESEND_AVAILABLE_AT", nullable = false)
  Instant resendAvailableAt;

  @PrePersist
  void prePersist() {
    Instant now = Instant.now();
    if (createdAt == null) {
      createdAt = now;
    }
    if (expiresAt == null) {
      expiresAt = createdAt.plusSeconds(300);
    }
    if (flowExpiresAt == null) {
      flowExpiresAt = createdAt.plusSeconds(300);
    }
    if (resendAvailableAt == null) {
      resendAvailableAt = createdAt.plusSeconds(30);
    }
    if (attemptsLeft == 0) {
      attemptsLeft = 5;
    }
  }
}