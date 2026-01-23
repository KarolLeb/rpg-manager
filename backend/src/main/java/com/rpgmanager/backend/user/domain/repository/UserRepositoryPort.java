package com.rpgmanager.backend.user.domain.repository;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
  List<UserDomain> findAll();

  Optional<UserDomain> findById(Long id);

  Optional<UserDomain> findByUsername(String username);

  UserDomain save(UserDomain user);

  boolean existsById(Long id);
}
