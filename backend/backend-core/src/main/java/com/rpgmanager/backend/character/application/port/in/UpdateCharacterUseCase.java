package com.rpgmanager.backend.character.application.port.in;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;

/** Use case for updating character details. */
public interface UpdateCharacterUseCase {

  /**
   * Updates an existing character.
   *
   * @param id the ID of the character to update
   * @param characterDetails the new character details
   * @return the updated character response
   */
  CharacterResponse updateCharacter(Long id, CharacterDomain characterDetails);
}
