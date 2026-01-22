package com.rpgmanager.backend.character.application.mapper;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class CharacterApplicationMapperTest {

    private final CharacterApplicationMapper mapper = Mappers.getMapper(CharacterApplicationMapper.class);

    @Test
    void shouldMapDomainToResponse() {
        CharacterDomain domain = CharacterDomain.builder()
                .uuid(java.util.UUID.randomUUID())
                .name("Conan")
                .characterClass("Barbarian")
                .level(1)
                .stats("{}")
                .ownerUsername("testuser")
                .campaignName("Test Campaign")
                .characterType(CharacterDomain.CharacterType.PERMANENT)
                .build();

        CharacterResponse response = mapper.toResponse(domain);

        assertNotNull(response);
        assertEquals("Conan", response.name());
        assertEquals("testuser", response.ownerName());
        assertEquals("Test Campaign", response.campaignName());
        assertEquals("PERMANENT", response.characterType());
    }

    @Test
    void shouldMapDomainWithNullRelations() {
        CharacterDomain domain = CharacterDomain.builder()
                .uuid(java.util.UUID.randomUUID())
                .name("Conan")
                .build();

        CharacterResponse response = mapper.toResponse(domain);

        assertNotNull(response);
        assertNull(response.ownerName());
        assertNull(response.campaignName());
        assertNull(response.characterType());
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(mapper.toResponse(null));
    }
}
