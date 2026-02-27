package com.example.demcayniki.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "ROLE")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


  @NotNull
    @NotBlank
    @Column(name = "ROLE", unique=true, nullable=false)
    private String role;

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Role role1)) {
      return false;
    }
    return id == role1.id && Objects.equals(role, role1.role);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, role);
  }

}
