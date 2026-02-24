package com.rpgmanager.auth.user.infrastructure.adapter.outgoing.persist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity representing a User in the database. */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @jakarta.persistence.ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
  @jakarta.persistence.CollectionTable(name = "user_roles", joinColumns = @jakarta.persistence.JoinColumn(name = "user_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "roles", nullable = false, length = 20)
  private java.util.Set<Role> roles;

  /** User role. */
  public enum Role {
    GM,
    PLAYER,
    ADMIN
  }
}
