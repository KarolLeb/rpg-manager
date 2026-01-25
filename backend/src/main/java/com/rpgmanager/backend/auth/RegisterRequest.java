package com.rpgmanager.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request object for user registration. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  private String username;
  private String password;
  private String email;
}
