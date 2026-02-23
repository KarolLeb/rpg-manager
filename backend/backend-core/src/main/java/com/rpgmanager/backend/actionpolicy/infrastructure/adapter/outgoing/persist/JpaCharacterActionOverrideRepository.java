package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCharacterActionOverrideRepository extends JpaRepository<CharacterActionOverrideEntity, Long> {
    Optional<CharacterActionOverrideEntity> findByCharacterIdAndActionTypeAndContextTypeAndContextId(Long characterId,
            ActionType actionType, ContextType contextType, Long contextId);
}
