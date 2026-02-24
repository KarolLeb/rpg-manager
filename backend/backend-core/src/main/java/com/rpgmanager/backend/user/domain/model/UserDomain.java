package com.rpgmanager.backend.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model representing a User (minimal for Core service). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDomain {
  private Long id;
  private String username;
  private String email;
  private java.util.Set<String> roles;

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public java.util.Set<String> getRoles() {
    return roles;
  }
}
