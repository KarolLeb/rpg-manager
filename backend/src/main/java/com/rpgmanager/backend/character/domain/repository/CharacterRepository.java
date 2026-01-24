package com.rpgmanager.backend.character.domain.repository;

import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import java.util.List;
import java.util.Optional;

public interface CharacterRepository {
  List<CharacterDomain> findAll();

  Optional<CharacterDomain> findById(Long id);

  CharacterDomain save(CharacterDomain character);
}
