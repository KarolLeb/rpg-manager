package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.rpgmanager.backend.actionpolicy.application.service.ActionPermissionService;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ActionPermissionControllerTest {

    @Mock
    private ActionPermissionService actionPermissionService;

    @InjectMocks
    private ActionPermissionController controller;

    @Test
    void checkPermission() {
        when(actionPermissionService.canPerformAction(1L, ActionType.LEVEL_UP, 2L, 3L))
                .thenReturn(true);

        ResponseEntity<ActionPermissionController.PermissionCheckResponse> response = controller.checkPermission(1L,
                ActionType.LEVEL_UP, 2L, 3L);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().allowed()).isTrue();
    }
}
