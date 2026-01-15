package com.example.demcayniki.domain.entity;


import com.example.demcayniki.model.constants.Role;
import jakarta.persistence.*;


@Entity
@Table(name = "ROLES")
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", unique=true, nullable=false)
    private Role role;
}
