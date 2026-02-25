package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.ActionPolicyEntity;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.CharacterActionOverrideEntity;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.JpaActionPolicyRepository;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.JpaCharacterActionOverrideRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AdminPolicyManagementControllerTest {

        @Mock
        private JpaActionPolicyRepository actionPolicyRepository;

        @Mock
        private JpaCharacterActionOverrideRepository characterActionOverrideRepository;

        @InjectMocks
        private AdminPolicyManagementController controller;

        @Test
        void setPolicy_shouldUpdateAllowedAndSave() {
                ActionPolicyEntity entity = new ActionPolicyEntity();
                when(actionPolicyRepository.findByActionTypeAndContextTypeAndContextId(
                                ActionType.LEVEL_UP, ContextType.CAMPAIGN, 1L))
                                .thenReturn(Optional.of(entity));

                ResponseEntity<Void> response = controller.setPolicy(ActionType.LEVEL_UP, ContextType.CAMPAIGN, 1L,
                                true);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                ArgumentCaptor<ActionPolicyEntity> captor = ArgumentCaptor.forClass(ActionPolicyEntity.class);
                verify(actionPolicyRepository).save(captor.capture());
                assertThat(captor.getValue().isAllowed()).isTrue();
        }

        @Test
        void setCharacterOverride_shouldUpdateAllowedAndSave() {
                CharacterActionOverrideEntity entity = new CharacterActionOverrideEntity();
                when(characterActionOverrideRepository.findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                                100L, ActionType.LEVEL_UP, ContextType.SESSION, 2L))
                                .thenReturn(Optional.of(entity));

                ResponseEntity<Void> response = controller.setCharacterOverride(100L, ActionType.LEVEL_UP,
                                ContextType.SESSION,
                                2L, false);

                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
                ArgumentCaptor<CharacterActionOverrideEntity> captor = ArgumentCaptor
                                .forClass(CharacterActionOverrideEntity.class);
                verify(characterActionOverrideRepository).save(captor.capture());
                assertThat(captor.getValue().isAllowed()).isFalse();
        }
}
