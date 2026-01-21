package com.rpgmanager.backend.character.domain.repository;

import com.rpgmanager.backend.character.domain.model.CharacterDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CharacterRepository {
    List<CharacterDomain> findAll();
    Optional<CharacterDomain> findByUuid(UUID uuid);
    CharacterDomain save(CharacterDomain character);
}
