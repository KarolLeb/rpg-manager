package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.actionpolicy.application.port.out.ActionPolicyRepositoryPort;
import com.rpgmanager.backend.actionpolicy.domain.ActionPolicy;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.infrastructure.mapper.ActionPolicyPersistenceMapper;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActionPolicyPersistenceAdapter implements ActionPolicyRepositoryPort {

  private final JpaActionPolicyRepository jpaRepository;
  private final ActionPolicyPersistenceMapper mapper;

  @Override
  public Optional<ActionPolicy> findByActionTypeAndContextTypeAndContextId(
      ActionType actionType, ContextType contextType, Long contextId) {
    return jpaRepository
        .findByActionTypeAndContextTypeAndContextId(actionType, contextType, contextId)
        .map(mapper::toDomain);
  }
}
