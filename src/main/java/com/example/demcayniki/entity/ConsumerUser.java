package com.example.demcayniki.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Data
public class ConsumerUser extends UserBase {

    @OneToMany(mappedBy = "follower")
    Set<Follow> following = new HashSet<>();

    @OneToMany(mappedBy = "followed")
    Set<Follow> followers = new HashSet<>();


}
