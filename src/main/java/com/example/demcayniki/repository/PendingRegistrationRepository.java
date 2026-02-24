package com.example.demcayniki.repository;

import com.example.demcayniki.domain.secondary.PendingRegistration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, UUID> {
  Optional<PendingRegistration> findByEmail(String email);
  void deleteByEmail(String email);

  List<PendingRegistration> findAllByEmail(String email);

  Optional<PendingRegistration>
  findTopByEmailOrderByCreatedAtDesc(String email);
}
