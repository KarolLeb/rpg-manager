package com.rpgmanager.auth.user.infrastructure.adapter.outgoing.persist;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** JPA Repository for User entities. */
public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username);
}
