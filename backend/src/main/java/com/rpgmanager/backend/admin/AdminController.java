package com.rpgmanager.backend.admin;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final UserRepositoryPort userRepository;

  @GetMapping("/users")
  public ResponseEntity<List<UserDomain>> getAllUsers() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  @GetMapping("/health")
  public ResponseEntity<String> getHealth() {
    return ResponseEntity.ok("Admin module is healthy");
  }
}
