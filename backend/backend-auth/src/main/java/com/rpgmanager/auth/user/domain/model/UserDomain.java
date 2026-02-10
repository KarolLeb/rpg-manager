package com.rpgmanager.auth.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model representing a User. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDomain {
  private Long id;
  private String username;
  private String password;
  private String email;
  private Role role;

  /** User role. */
  public enum Role {
    GM,
    PLAYER,
    ADMIN
  }
}
