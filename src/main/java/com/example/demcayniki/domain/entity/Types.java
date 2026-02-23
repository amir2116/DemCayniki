package com.example.demcayniki.domain.entity;

import com.example.demcayniki.model.constants.modifiable.UserType;
import jakarta.persistence.*;

@Entity
@Table(name = "TYPES")
public class Types {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_TYPE", unique=true, nullable=false)
    private UserType userType;
}
