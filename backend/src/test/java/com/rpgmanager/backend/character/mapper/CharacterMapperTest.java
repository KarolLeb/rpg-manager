package com.rpgmanager.backend.character.mapper;

import com.rpgmanager.backend.campaign.Campaign;
import com.rpgmanager.backend.character.Character;
import com.rpgmanager.backend.character.dto.CharacterResponse;
import com.rpgmanager.backend.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterMapperTest {

    @Test
    void shouldMapCharacterToResponse() {
        User user = User.builder().username("testuser").build();
        Campaign campaign = Campaign.builder().name("Test Campaign").build();
        Character character = new Character();
        character.setUuid(java.util.UUID.randomUUID());
        character.setName("Conan");
        character.setCharacterClass("Barbarian");
        character.setLevel(1);
        character.setStats("{}");
        character.setUser(user);
        character.setCampaign(campaign);
        character.setCharacterType(Character.CharacterType.PERMANENT);

        CharacterResponse response = CharacterMapper.toResponse(character);

        assertNotNull(response);
        assertEquals("Conan", response.name());
        assertEquals("testuser", response.ownerName());
        assertEquals("Test Campaign", response.campaignName());
        assertEquals("PERMANENT", response.characterType());
    }

    @Test
    void shouldMapCharacterWithNullRelations() {
        Character character = new Character();
        character.setUuid(java.util.UUID.randomUUID());
        character.setName("Conan");
        character.setCharacterType(null); // Explicitly set to null to override default

        CharacterResponse response = CharacterMapper.toResponse(character);

        assertNotNull(response);
        assertNull(response.ownerName());
        assertNull(response.campaignName());
        assertNull(response.characterType());
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(CharacterMapper.toResponse(null));
    }
}
