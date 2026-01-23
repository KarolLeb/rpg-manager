package com.rpgmanager.backend.character.application.port.in;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import java.util.UUID;

public interface UpdateCharacterUseCase {
  CharacterResponse updateCharacter(UUID uuid, CharacterDomain characterDetails);
}
