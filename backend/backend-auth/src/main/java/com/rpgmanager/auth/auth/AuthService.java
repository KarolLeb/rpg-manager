package com.rpgmanager.auth.auth;

import com.rpgmanager.auth.security.JwtUtil;
import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for handling authentication and user registration. */
@Service
@RequiredArgsConstructor
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
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    UserDomain user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    String token = jwtUtil.generateToken(user.getUsername(), user.getId(), user.getRole().name());

    return new AuthResponse(token, user.getUsername(), user.getRole().name());
  }

  /**
   * Registers a new user.
   *
   * @param request the registration request containing user details
   */
  @Transactional
  public void register(RegisterRequest request) {
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new IllegalArgumentException("Username already exists");
    }

    UserDomain user = new UserDomain();
    user.setUsername(request.getUsername());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEmail(request.getEmail());
    user.setRole(UserDomain.Role.PLAYER);

    userRepository.save(user);
  }
}
