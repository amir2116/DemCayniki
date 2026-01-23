package com.example.demcayniki.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
public class ConsumerUser extends UserBase {

    @OneToMany(mappedBy = "follower")
    Set<Follow> following = new HashSet<>();

    @OneToMany(mappedBy = "followed")
    Set<Follow> followers = new HashSet<>();


}
