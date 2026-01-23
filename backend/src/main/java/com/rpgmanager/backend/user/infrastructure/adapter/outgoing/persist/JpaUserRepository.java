package com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
}
