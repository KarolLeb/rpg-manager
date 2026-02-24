package com.rpgmanager.backend.actionpolicy.application.port.out;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.CharacterActionOverride;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import java.util.Optional;

public interface CharacterActionOverrideRepositoryPort {
  Optional<CharacterActionOverride> findByCharacterIdAndActionTypeAndContextTypeAndContextId(
      Long characterId, ActionType actionType, ContextType contextType, Long contextId);
}
