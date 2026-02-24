package com.example.demcayniki.domain.secondary;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;
import lombok.*;
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
public class PendingRegistration{

  @Id
  @UuidGenerator
  @Column(name = "ID", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "EMAIL", nullable = false, length = 320)
  private String email;

  // store HASH, not raw code
  @Column(name = "CODE_HASH", nullable = false, length = 64)
  private String codeHash;

  @Column(name = "VERIFIED", nullable = false)
  private boolean verified;

  @Column(name = "ATTEMPTS_LEFT", nullable = false)
  private int attemptsLeft;

  @Column(name = "CREATED_AT", nullable = false)
  private Instant createdAt;

  @Column(name = "EXPIRES_AT", nullable = false)
  private Instant expiresAt;

  // rate limit resend
  @Column(name = "RESEND_AVAILABLE_AT", nullable = false)
  private Instant resendAvailableAt;

  // after verify step, you can issue a flow token for "complete registration"
  @Column(name = "FLOW_TOKEN_HASH", length = 64)
  private String flowTokenHash;

  @Column(name = "FLOW_EXPIRES_AT")
  private Instant flowExpiresAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }


}
