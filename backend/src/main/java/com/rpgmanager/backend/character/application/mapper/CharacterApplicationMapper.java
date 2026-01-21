package com.rpgmanager.backend.character.application.mapper;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;

public class CharacterApplicationMapper {
    
    public static CharacterResponse toResponse(CharacterDomain domain) {
        if (domain == null) {
            return null;
        }

        return new CharacterResponse(
            domain.getUuid(),
            domain.getName(),
            domain.getCharacterClass(),
            domain.getLevel(),
            domain.getStats(),
            domain.getOwnerUsername(),
            domain.getCampaignName(),
            domain.getCharacterType() != null ? domain.getCharacterType().name() : null
        );
    }
}
