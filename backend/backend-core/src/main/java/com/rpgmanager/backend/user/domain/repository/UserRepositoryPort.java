package com.rpgmanager.backend.user.domain.repository;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import java.util.Optional;

/** Port for user service operations. */
public interface UserRepositoryPort {
  Optional<UserDomain> findById(Long id);
  Optional<UserDomain> findByUsername(String username);
  java.util.List<UserDomain> findAll();
}
