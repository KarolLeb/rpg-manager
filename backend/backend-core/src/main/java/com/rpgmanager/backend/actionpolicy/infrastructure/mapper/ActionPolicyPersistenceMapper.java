package com.rpgmanager.backend.actionpolicy.infrastructure.mapper;

import com.rpgmanager.backend.actionpolicy.domain.ActionPolicy;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.ActionPolicyEntity;
import org.springframework.stereotype.Component;

@Component
public class ActionPolicyPersistenceMapper {

  public ActionPolicy toDomain(ActionPolicyEntity entity) {
    if (entity == null) {
      return null;
    }
    return ActionPolicy.builder()
        .id(entity.getId())
        .actionType(entity.getActionType())
        .contextType(entity.getContextType())
        .contextId(entity.getContextId())
        .isAllowed(entity.isAllowed())
        .build();
  }

  public ActionPolicyEntity toEntity(ActionPolicy domain) {
    if (domain == null) {
      return null;
    }
    return ActionPolicyEntity.builder()
        .id(domain.getId())
        .actionType(domain.getActionType())
        .contextType(domain.getContextType())
        .contextId(domain.getContextId())
        .isAllowed(domain.isAllowed())
        .build();
  }
}
