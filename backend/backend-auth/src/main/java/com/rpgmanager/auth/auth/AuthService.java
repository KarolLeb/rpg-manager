package com.rpgmanager.auth.auth;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.domain.repository.UserRepositoryPort;
import com.rpgmanager.common.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for handling authentication and user registration. */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserRepositoryPort userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Authenticates a user and generates a JWT token.
   *
   * @param request the login request containing username and password
   * @return the authentication response containing the JWT token
   */
  public AuthResponse login(LoginRequest request) {
    log.info("Attempting login for user: {}", request.getUsername());
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    } catch (Exception e) {
      log.error(
          "Authentication failed for user: {}. Error: {}", request.getUsername(), e.getMessage());
      throw e;
    }

    UserDomain user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(
                () -> {
                  log.error("User not found after authentication: {}", request.getUsername());
                  return new UsernameNotFoundException("User not found");
                });

    String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());
    log.info("Login successful for user: {}", request.getUsername());

    return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getId());
  }

  /**
   * Registers a new user.
   *
   * @param request the registration request containing user details
   */
  @Transactional
  public void register(RegisterRequest request) {
    log.info("Registering user: {}", request.getUsername());
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      log.warn("Registration failed: Username already exists: {}", request.getUsername());
      throw new IllegalArgumentException("Username already exists");
    }

    UserDomain user = new UserDomain();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setRole(UserDomain.Role.PLAYER);

    userRepository.save(user);
    log.info("User registered successfully: {}", request.getUsername());
  }
}
