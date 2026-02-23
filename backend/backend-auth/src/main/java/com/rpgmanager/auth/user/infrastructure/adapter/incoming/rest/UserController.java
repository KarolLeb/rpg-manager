package com.rpgmanager.auth.user.infrastructure.adapter.incoming.rest;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepositoryPort userRepository;

  @GetMapping("/{id}")
  public ResponseEntity<UserDomain> getUserById(@PathVariable @NonNull Long id) {
    return userRepository
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<UserDomain> getUserByUsername(@RequestParam @NonNull String username) {
    return userRepository
        .findByUsername(username)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/all")
  public ResponseEntity<java.util.List<UserDomain>> getAllUsers() {
    return ResponseEntity.ok(userRepository.findAll());
  }
}
