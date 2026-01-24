package com.rpgmanager.backend.character.application.port.in;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import java.util.List;

public interface GetCharacterUseCase {
  List<CharacterResponse> getAllCharacters();

  CharacterResponse getCharacter(Long id);
}
