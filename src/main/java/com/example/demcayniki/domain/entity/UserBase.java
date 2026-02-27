package com.example.demcayniki.domain.entity;


import com.example.demcayniki.model.constants.modifiable.RoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;


@Entity
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class UserBase {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

  @Column(name = "FIRSTNAME", nullable = false)
    private String firstName;
    @Column(name = "LASTNAME")
    private String lastName;

  @Column(name = "BIRTH_DATE")
  private Instant birthDate;

  @Column(name = "SEX_CODE")
  private int sexCode;

  @Column(name = "EMAIL", nullable = false, unique = true, length = 320)
    private String email;

  @Column(name = "PASSWORD", nullable = false, length = 64)
    private String password;

    @Column
    private int status;

  @Column(name = "CREATE_BY", nullable = false)
  private String createdBy = "SELF";
  @Column(name = "CREATE_DATE", nullable = false)
  private Instant createdAt;
  @Column(name = "UPDATE_BY")
  private String updatedBy = "SELF";
  @Column(name = "UPDATE_DATE")
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
  private Set<Role> roles = new HashSet<>();

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }


  public void grantRole(RoleEnum role) {
    roles.add(new Role(role.getCode(),role.name()));
  }

  public void revokeRole(RoleEnum role) {
    roles.remove(new Role(role.getCode(),role.name()));
  }

  public void grantRole(List<RoleEnum> roleEnumList){
    for (RoleEnum roleEnum : roleEnumList) {
      roles.add(new Role(roleEnum.getCode(),roleEnum.name()));
    }
  }

  public void revokeRole(List<RoleEnum> roleEnumList){
    for (RoleEnum roleEnum : roleEnumList) {
      roles.remove(new Role(roleEnum.getCode(),roleEnum.name()));
    }
  }
}
