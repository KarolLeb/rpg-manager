package com.rpgmanager.admin.admin;

import com.rpgmanager.admin.user.domain.model.UserDomain;
import com.rpgmanager.admin.user.domain.repository.UserRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for administrative operations. */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  private final UserRepositoryPort userRepository;

  /**
   * Retrieves all users.
   *
   * @return a response entity containing a list of all users
   */
  @GetMapping("/users")
  public ResponseEntity<List<UserDomain>> getAllUsers() {
    return ResponseEntity.ok(userRepository.findAll());
  }

  /**
   * Checks the health of the admin module.
   *
   * @return a response entity containing a health message
   */
  @GetMapping("/health")
  public ResponseEntity<String> getHealth() {
    return ResponseEntity.ok("Admin module is healthy");
  }
}
