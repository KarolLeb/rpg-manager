package com.rpgmanager.backend.character.domain.repository;

import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import java.util.List;
import java.util.Optional;

/** Repository port for Character domain. */
public interface CharacterRepository {
  /**
   * Retrieves all characters.
   *
   * @return a list of all characters
   */
  List<CharacterDomain> findAll();

  /**
   * Finds a character by ID.
   *
   * @param id the character ID
   * @return an optional containing the character if found
   */
  Optional<CharacterDomain> findById(Long id);

  /**
   * Saves a character.
   *
   * @param character the character to save
   * @return the saved character
   */
  CharacterDomain save(CharacterDomain character);
}
