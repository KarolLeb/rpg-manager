package com.rpgmanager.auth.auth;

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
  private java.util.List<String> roles;
  private Long id;
}
