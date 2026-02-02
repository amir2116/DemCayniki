package com.example.demcayniki.repository;

import com.example.demcayniki.entity.ConsumerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConsumerUserRepository extends JpaRepository<ConsumerUser, UUID> {
    Optional<ConsumerUser> findByEmailIgnoreCase(String email);

}
