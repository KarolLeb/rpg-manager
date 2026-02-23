package com.rpgmanager.backend.actionpolicy.application.service;

import com.rpgmanager.backend.actionpolicy.application.port.out.ActionPolicyRepositoryPort;
import com.rpgmanager.backend.actionpolicy.application.port.out.CharacterActionOverrideRepositoryPort;
import com.rpgmanager.backend.actionpolicy.domain.ActionPolicy;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.domain.CharacterActionOverride;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActionPermissionService {

    private final ActionPolicyRepositoryPort actionPolicyRepositoryPort;
    private final CharacterActionOverrideRepositoryPort characterActionOverrideRepositoryPort;

    public boolean canPerformAction(Long characterId, ActionType actionType, Long campaignId, Long sessionId,
            Long dialogueId) {
        // 1. Check Character Overrides (from most specific to least specific)
        if (sessionId != null) {
            Optional<CharacterActionOverride> sessionOverride = characterActionOverrideRepositoryPort
                    .findByCharacterIdAndActionTypeAndContextTypeAndContextId(characterId, actionType,
                            ContextType.SESSION,
                            sessionId);
            if (sessionOverride.isPresent()) {
                return sessionOverride.get().isAllowed();
            }
        }

        if (campaignId != null) {
            Optional<CharacterActionOverride> campaignOverride = characterActionOverrideRepositoryPort
                    .findByCharacterIdAndActionTypeAndContextTypeAndContextId(characterId, actionType,
                            ContextType.CAMPAIGN,
                            campaignId);
            if (campaignOverride.isPresent()) {
                return campaignOverride.get().isAllowed();
            }
        }

        // 2. Check Context Policies (from most specific to least specific)
        if (sessionId != null) {
            Optional<ActionPolicy> sessionPolicy = actionPolicyRepositoryPort
                    .findByActionTypeAndContextTypeAndContextId(actionType, ContextType.SESSION, sessionId);
            if (sessionPolicy.isPresent()) {
                return sessionPolicy.get().isAllowed();
            }
        }

        if (campaignId != null) {
            Optional<ActionPolicy> campaignPolicy = actionPolicyRepositoryPort
                    .findByActionTypeAndContextTypeAndContextId(actionType, ContextType.CAMPAIGN, campaignId);
            if (campaignPolicy.isPresent()) {
                return campaignPolicy.get().isAllowed();
            }
        }

        // 3. Default status
        return true;
    }
}
