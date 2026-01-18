package com.rpgmanager.backend.character.mapper;

import com.rpgmanager.backend.character.Character;
import com.rpgmanager.backend.character.dto.CharacterResponse;

public class CharacterMapper {
    
    public static CharacterResponse toResponse(Character character) {
        if (character == null) {
            return null;
        }

        return new CharacterResponse(
            character.getUuid(),
            character.getName(),
            character.getCharacterClass(),
            character.getLevel(),
            character.getStats(),
            character.getUser() != null ? character.getUser().getUsername() : null,
            character.getCampaign() != null ? character.getCampaign().getName() : null,
            character.getCharacterType() != null ? character.getCharacterType().name() : null
        );
    }
}
