package com.rpgmanager.backend.character.application.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CharacterApplicationMapperTest {

  private final CharacterApplicationMapper mapper =
      Mappers.getMapper(CharacterApplicationMapper.class);

  @Test
  void toResponse_shouldMapAllFields() {
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setCharacterType(CharacterDomain.CharacterType.PERMANENT);
    domain.setOwnerUsername("testuser");

    CharacterResponse response = mapper.toResponse(domain);

    assertThat(response).isNotNull();
    assertThat(response.characterType()).isEqualTo("PERMANENT");
    assertThat(response.ownerName()).isEqualTo("testuser");
  }

  @Test
  void toResponse_shouldHandleNull() {
    assertThat(mapper.toResponse(null)).isNull();
  }
}
