package com.example.demcayniki.repository;

import com.example.demcayniki.entity.ConsumerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsumerUserRepository extends JpaRepository<ConsumerUser, UUID> {

    Optional<ConsumerUser> findByEmailIgnoreCase(String email);
}
