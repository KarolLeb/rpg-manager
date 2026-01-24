package com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCharacterRepository extends JpaRepository<CharacterEntity, Long> {}
