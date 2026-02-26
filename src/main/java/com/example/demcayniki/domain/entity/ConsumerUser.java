package com.example.demcayniki.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerUser extends UserBase {

    @OneToMany(mappedBy = "follower")
    Set<Follow> following = new HashSet<>();

    @OneToMany(mappedBy = "followed")
    Set<Follow> followers = new HashSet<>();


}
