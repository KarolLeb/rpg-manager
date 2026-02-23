package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.in.web;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.ActionPolicyEntity;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.JpaActionPolicyRepository;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.JpaCharacterActionOverrideRepository;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.CharacterActionOverrideEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/permissions/admin")
@RequiredArgsConstructor
public class AdminPolicyManagementController {

        private final JpaActionPolicyRepository actionPolicyRepository;
        private final JpaCharacterActionOverrideRepository characterActionOverrideRepository;

        @PostMapping("/policy")
        public ResponseEntity<Void> setPolicy(
                        @RequestParam ActionType actionType,
                        @RequestParam ContextType contextType,
                        @RequestParam Long contextId,
                        @RequestParam boolean isAllowed) {

                ActionPolicyEntity entity = actionPolicyRepository
                                .findByActionTypeAndContextTypeAndContextId(actionType, contextType, contextId)
                                .orElse(ActionPolicyEntity.builder()
                                                .actionType(actionType)
                                                .contextType(contextType)
                                                .contextId(contextId)
                                                .build());

                entity.setAllowed(isAllowed);
                actionPolicyRepository.save(entity);

                return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        @PostMapping("/override")
        public ResponseEntity<Void> setCharacterOverride(
                        @RequestParam Long characterId,
                        @RequestParam ActionType actionType,
                        @RequestParam ContextType contextType,
                        @RequestParam Long contextId,
                        @RequestParam boolean isAllowed) {

                CharacterActionOverrideEntity entity = characterActionOverrideRepository
                                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(characterId, actionType,
                                                contextType,
                                                contextId)
                                .orElse(CharacterActionOverrideEntity.builder()
                                                .characterId(characterId)
                                                .actionType(actionType)
                                                .contextType(contextType)
                                                .contextId(contextId)
                                                .build());

                entity.setAllowed(isAllowed);
                characterActionOverrideRepository.save(entity);

                return ResponseEntity.status(HttpStatus.CREATED).build();
        }
}
