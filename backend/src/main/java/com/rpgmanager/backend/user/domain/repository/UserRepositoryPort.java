package com.rpgmanager.backend.user.domain.repository;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import java.util.List;
import java.util.Optional;

/** Port interface for User repository. */
public interface UserRepositoryPort {
  /**
   * Retrieves all users.
   *
   * @return a list of all users
   */
  List<UserDomain> findAll();

  /**
   * Finds a user by ID.
   *
   * @param id the user ID
   * @return an optional containing the user if found
   */
  Optional<UserDomain> findById(Long id);

  /**
   * Finds a user by username.
   *
   * @param username the username
   * @return an optional containing the user if found
   */
  Optional<UserDomain> findByUsername(String username);

  /**
   * Saves a user.
   *
   * @param user the user to save
   * @return the saved user
   */
  UserDomain save(UserDomain user);

  /**
   * Checks if a user exists by ID.
   *
   * @param id the user ID
   * @return true if the user exists, false otherwise
   */
  boolean existsById(Long id);
}
