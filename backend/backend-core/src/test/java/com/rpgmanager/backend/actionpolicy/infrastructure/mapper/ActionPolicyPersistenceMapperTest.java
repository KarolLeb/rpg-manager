package com.rpgmanager.backend.actionpolicy.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.actionpolicy.domain.ActionPolicy;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.ActionPolicyEntity;
import org.junit.jupiter.api.Test;

class ActionPolicyPersistenceMapperTest {

  private final ActionPolicyPersistenceMapper mapper = new ActionPolicyPersistenceMapper();

  @Test
  void shouldMapToDomain() {
    ActionPolicyEntity entity =
        ActionPolicyEntity.builder()
            .id(1L)
            .actionType(ActionType.LEVEL_UP)
            .contextType(ContextType.CAMPAIGN)
            .contextId(100L)
            .isAllowed(true)
            .build();

    ActionPolicy domain = mapper.toDomain(entity);

    assertThat(domain.getId()).isEqualTo(1L);
    assertThat(domain.getActionType()).isEqualTo(ActionType.LEVEL_UP);
    assertThat(domain.getContextType()).isEqualTo(ContextType.CAMPAIGN);
    assertThat(domain.getContextId()).isEqualTo(100L);
    assertThat(domain.isAllowed()).isTrue();
  }

  @Test
  void shouldMapToEntity() {
    ActionPolicy domain =
        ActionPolicy.builder()
            .id(1L)
            .actionType(ActionType.LEVEL_UP)
            .contextType(ContextType.CAMPAIGN)
            .contextId(100L)
            .isAllowed(false)
            .build();

    ActionPolicyEntity entity = mapper.toEntity(domain);

    assertThat(entity.getId()).isEqualTo(1L);
    assertThat(entity.getActionType()).isEqualTo(ActionType.LEVEL_UP);
    assertThat(entity.getContextType()).isEqualTo(ContextType.CAMPAIGN);
    assertThat(entity.getContextId()).isEqualTo(100L);
    assertThat(entity.isAllowed()).isFalse();
  }

  @Test
  void shouldReturnNullWhenMappingNull() {
    assertThat(mapper.toDomain(null)).isNull();
    assertThat(mapper.toEntity(null)).isNull();
  }
}
