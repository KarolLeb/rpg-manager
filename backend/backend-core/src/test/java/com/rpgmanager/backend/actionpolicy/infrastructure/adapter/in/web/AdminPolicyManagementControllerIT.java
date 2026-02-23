package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.in.web;

import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.ActionPolicyEntity;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.JpaActionPolicyRepository;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.JpaCharacterActionOverrideRepository;
import com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist.CharacterActionOverrideEntity;
import com.rpgmanager.backend.config.SecurityProperties;
import com.rpgmanager.common.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminPolicyManagementController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminPolicyManagementControllerIT {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JpaActionPolicyRepository actionPolicyRepository;

        @MockitoBean
        private JpaCharacterActionOverrideRepository characterActionOverrideRepository;

        @MockitoBean
        private SecurityProperties securityProperties;

        @MockitoBean
        private JwtUtil jwtUtil;

        @Test
        void shouldSetPolicySuccessfully() throws Exception {
                // given
                when(actionPolicyRepository.findByActionTypeAndContextTypeAndContextId(any(), any(), any()))
                                .thenReturn(Optional.empty());

                // when & then
                mockMvc.perform(post("/api/v1/permissions/admin/policy")
                                .param("actionType", "LEVEL_UP")
                                .param("contextType", "CAMPAIGN")
                                .param("contextId", "1")
                                .param("isAllowed", "true"))
                                .andExpect(status().isCreated());

                verify(actionPolicyRepository).save(any(ActionPolicyEntity.class));
        }

        @Test
        void shouldSetCharacterOverrideSuccessfully() throws Exception {
                // given
                when(characterActionOverrideRepository.findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(),
                                any(), any(), any()))
                                .thenReturn(Optional.empty());

                // when & then
                mockMvc.perform(post("/api/v1/permissions/admin/override")
                                .param("characterId", "100")
                                .param("actionType", "DISTRIBUTE_POINTS")
                                .param("contextType", "SESSION")
                                .param("contextId", "2")
                                .param("isAllowed", "false"))
                                .andExpect(status().isCreated());

                verify(characterActionOverrideRepository).save(any(CharacterActionOverrideEntity.class));
        }
}
