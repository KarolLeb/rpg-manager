package com.rpgmanager.backend.actionpolicy.infrastructure.mapper;

import com.rpgmanager.backend.actionpolicy.domain.CharacterActionOverride;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.CharacterActionOverrideEntity;
import org.springframework.stereotype.Component;

@Component
public class CharacterActionOverridePersistenceMapper {

  public CharacterActionOverride toDomain(CharacterActionOverrideEntity entity) {
    if (entity == null) {
      return null;
    }
    return CharacterActionOverride.builder()
        .id(entity.getId())
        .characterId(entity.getCharacterId())
        .actionType(entity.getActionType())
        .contextType(entity.getContextType())
        .contextId(entity.getContextId())
        .isAllowed(entity.isAllowed())
        .build();
  }

  public CharacterActionOverrideEntity toEntity(CharacterActionOverride domain) {
    if (domain == null) {
      return null;
    }
    return CharacterActionOverrideEntity.builder()
        .id(domain.getId())
        .characterId(domain.getCharacterId())
        .actionType(domain.getActionType())
        .contextType(domain.getContextType())
        .contextId(domain.getContextId())
        .isAllowed(domain.isAllowed())
        .build();
  }
}
