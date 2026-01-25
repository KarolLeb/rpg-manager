package com.rpgmanager.backend.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response object for authentication. */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
  private String token;
  private String username;
  private String role;
}
