package com.rpgmanager.backend.character.application.port.in;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;

public interface UpdateCharacterUseCase {
  CharacterResponse updateCharacter(Long id, CharacterDomain characterDetails);
}
