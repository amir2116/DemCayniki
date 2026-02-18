package com.example.demcayniki.repository;

import com.example.demcayniki.domain.secondary.PendingRegistration;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, UUID> {
  Optional<PendingRegistration> findByEmail(String email);
  void deleteByEmail(String email);
}
