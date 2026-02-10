package com.rpgmanager.admin.user.domain.repository;

import com.rpgmanager.admin.user.domain.model.UserDomain;
import java.util.List;
import java.util.Optional;

/** Port for user repository operations. */
public interface UserRepositoryPort {
  Optional<UserDomain> findByUsername(String username);

  UserDomain save(UserDomain user);

  List<UserDomain> findAll();
}
