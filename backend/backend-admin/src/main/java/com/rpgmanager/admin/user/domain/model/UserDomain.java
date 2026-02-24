package com.rpgmanager.admin.user.domain.model;

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
  private java.util.Set<Role> roles;

  /** User role. */
  public enum Role {
    GM,
    PLAYER,
    ADMIN
  }
}
