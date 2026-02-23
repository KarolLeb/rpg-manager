package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.actionpolicy.application.port.out.CharacterActionOverrideRepositoryPort;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.domain.CharacterActionOverride;
import com.rpgmanager.backend.actionpolicy.infrastructure.mapper.CharacterActionOverridePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CharacterActionOverridePersistenceAdapter implements CharacterActionOverrideRepositoryPort {

    private final JpaCharacterActionOverrideRepository jpaRepository;
    private final CharacterActionOverridePersistenceMapper mapper;

    @Override
    public Optional<CharacterActionOverride> findByCharacterIdAndActionTypeAndContextTypeAndContextId(Long characterId,
            ActionType actionType, ContextType contextType, Long contextId) {
        return jpaRepository
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(characterId, actionType, contextType,
                        contextId)
                .map(mapper::toDomain);
    }
}
