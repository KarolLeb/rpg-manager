package com.rpgmanager.backend.character.application.port.in;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import java.util.List;

/** Use case for retrieving character information. */
public interface GetCharacterUseCase {

  /**
   * Retrieves all characters.
   *
   * @return a list of character responses
   */
  List<CharacterResponse> getAllCharacters();

  /**
   * Retrieves a specific character by ID.
   *
   * @param id the character ID
   * @return the character response
   */
  CharacterResponse getCharacter(Long id);
}
