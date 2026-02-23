package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rpgmanager.backend.actionpolicy.application.service.ActionPermissionService;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.config.SecurityProperties;
import com.rpgmanager.common.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ActionPermissionController.class)
@AutoConfigureMockMvc(
    addFilters = false) // Add filters false bypasses Spring Security for this unit-level controller
// test
class ActionPermissionControllerIT {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ActionPermissionService actionPermissionService;

  @MockitoBean private SecurityProperties securityProperties;

  @MockitoBean private JwtUtil jwtUtil;

  @Test
  void shouldReturnTrueWhenServiceAllowsAction() throws Exception {
    // given
    when(actionPermissionService.canPerformAction(eq(1L), eq(ActionType.LEVEL_UP), any(), any()))
        .thenReturn(true);

    // when & then
    mockMvc
        .perform(
            get("/api/v1/permissions/check")
                .param("characterId", "1")
                .param("actionType", "LEVEL_UP"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.allowed").value(true));
  }

  @Test
  void shouldReturnFalseWhenServiceDeniesAction() throws Exception {
    // given
    when(actionPermissionService.canPerformAction(
            eq(2L), eq(ActionType.DISTRIBUTE_POINTS), eq(100L), any()))
        .thenReturn(false);

    // when & then
    mockMvc
        .perform(
            get("/api/v1/permissions/check")
                .param("characterId", "2")
                .param("actionType", "DISTRIBUTE_POINTS")
                .param("campaignId", "100"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.allowed").value(false));
  }
}
