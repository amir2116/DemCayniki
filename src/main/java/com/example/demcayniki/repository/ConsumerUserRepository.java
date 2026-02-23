package com.example.demcayniki.repository;

import com.example.demcayniki.domain.entity.ConsumerUser;
import java.lang.ScopedValue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsumerUserRepository extends JpaRepository<ConsumerUser, UUID> {
    Optional<ConsumerUser> findByEmailIgnoreCase(String email);

  <T> ScopedValue<T> findAllByEmailIgnoreCase(String email);

  List<ConsumerUser> findAllByEmail(String email);
}
