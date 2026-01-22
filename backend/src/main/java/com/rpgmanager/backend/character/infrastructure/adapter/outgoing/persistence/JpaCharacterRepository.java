package com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCharacterRepository extends JpaRepository<CharacterEntity, Long> {
    Optional<CharacterEntity> findByUuid(UUID uuid);
}
