package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaActionPolicyRepository extends JpaRepository<ActionPolicyEntity, Long> {
    Optional<ActionPolicyEntity> findByActionTypeAndContextTypeAndContextId(ActionType actionType,
            ContextType contextType, Long contextId);
}
