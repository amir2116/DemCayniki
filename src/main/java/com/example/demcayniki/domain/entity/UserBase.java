package com.example.demcayniki.domain.entity;


import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class UserBase {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "FIRSTNAME")
    private String firstName;
    @Column(name = "LASTNAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;

    @Column
    private int status;

    @Column(name = "CREATE_BY")
    private String createdBy;
    @Column(name = "CREATE_DATE")
    private Instant createdAt;
    @Column(name = "UPDATE_BY")
    private String updatedBy;
    @Column(name= "UPDATE_DATE")
    private Instant updatedAt;

    @ManyToMany
    @JoinTable(
            name = "USER_TYPES_MAP",
            joinColumns = @JoinColumn(name="USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "TYPE_ID")
    )
    private Set<Types> userTypes = new HashSet<>();

    @ManyToMany()
            @JoinTable(
                    name = "USER_ROLES_MAP",
                    joinColumns = @JoinColumn(name = "USER_ID"),
                    inverseJoinColumns = @JoinColumn(name="ROLE_ID")
            )
    private Set<Roles> roles = new HashSet<>();
}
