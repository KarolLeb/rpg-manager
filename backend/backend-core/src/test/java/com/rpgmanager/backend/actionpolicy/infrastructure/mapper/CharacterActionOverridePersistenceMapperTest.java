package com.rpgmanager.backend.actionpolicy.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.CharacterActionOverride;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.CharacterActionOverrideEntity;
import org.junit.jupiter.api.Test;

class CharacterActionOverridePersistenceMapperTest {

  private final CharacterActionOverridePersistenceMapper mapper =
      new CharacterActionOverridePersistenceMapper();

  @Test
  void shouldMapToDomain() {
    CharacterActionOverrideEntity entity =
        CharacterActionOverrideEntity.builder()
            .id(1L)
            .characterId(50L)
            .actionType(ActionType.DISTRIBUTE_POINTS)
            .contextType(ContextType.SESSION)
            .contextId(200L)
            .isAllowed(true)
            .build();

    CharacterActionOverride domain = mapper.toDomain(entity);

    assertThat(domain.getId()).isEqualTo(1L);
    assertThat(domain.getCharacterId()).isEqualTo(50L);
    assertThat(domain.getActionType()).isEqualTo(ActionType.DISTRIBUTE_POINTS);
    assertThat(domain.getContextType()).isEqualTo(ContextType.SESSION);
    assertThat(domain.getContextId()).isEqualTo(200L);
    assertThat(domain.isAllowed()).isTrue();
  }

  @Test
  void shouldMapToEntity() {
    CharacterActionOverride domain =
        CharacterActionOverride.builder()
            .id(1L)
            .characterId(50L)
            .actionType(ActionType.DISTRIBUTE_POINTS)
            .contextType(ContextType.SESSION)
            .contextId(200L)
            .isAllowed(false)
            .build();

    CharacterActionOverrideEntity entity = mapper.toEntity(domain);

    assertThat(entity.getId()).isEqualTo(1L);
    assertThat(entity.getCharacterId()).isEqualTo(50L);
    assertThat(entity.getActionType()).isEqualTo(ActionType.DISTRIBUTE_POINTS);
    assertThat(entity.getContextType()).isEqualTo(ContextType.SESSION);
    assertThat(entity.getContextId()).isEqualTo(200L);
    assertThat(entity.isAllowed()).isFalse();
  }

  @Test
  void shouldReturnNullWhenMappingNull() {
    assertThat(mapper.toDomain(null)).isNull();
    assertThat(mapper.toEntity(null)).isNull();
  }
}
