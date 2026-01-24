package com.rpgmanager.backend.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  public enum Role {
    GM,
    PLAYER
  }
}
