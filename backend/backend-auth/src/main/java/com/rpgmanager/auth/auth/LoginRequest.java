package com.rpgmanager.auth.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request object for user login. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
  private String username;
  private String password;
}
