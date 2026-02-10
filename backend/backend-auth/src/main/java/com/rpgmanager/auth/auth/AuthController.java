package com.rpgmanager.auth.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for authentication operations. */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * Authenticates a user.
   *
   * @param request the login request
   * @return a response entity containing the auth response
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  /**
   * Registers a new user.
   *
   * @param request the registration request
   * @return a response entity with a success message
   */
  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.ok("User registered successfully");
  }
}
