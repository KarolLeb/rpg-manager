package com.rpgmanager.backend.actionpolicy.application.port.out;

import com.rpgmanager.backend.actionpolicy.domain.ActionPolicy;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import java.util.Optional;

public interface ActionPolicyRepositoryPort {
  Optional<ActionPolicy> findByActionTypeAndContextTypeAndContextId(
      ActionType actionType, ContextType contextType, Long contextId);
}
