package com.rpgmanager.backend.activitylog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityEventListenerTest {

  @Mock private ActivityLogService activityLogService;

  @InjectMocks private ActivityEventListener activityEventListener;

  @Test
  void handleActivityEvent_shouldDelegateToActivityLogService() {
    ActivityEvent event =
        new ActivityEvent(
            ActivityLogEntry.ActionType.DICE_ROLL,
            "Rolled d20, got 18",
            1L,
            2L,
            3L,
            Map.of("sides", 20, "result", 18));

    activityEventListener.handleActivityEvent(event);

    org.mockito.ArgumentCaptor<CreateActivityLogRequest> captor =
        org.mockito.ArgumentCaptor.forClass(CreateActivityLogRequest.class);
    verify(activityLogService).logActivity(captor.capture());

    CreateActivityLogRequest request = captor.getValue();
    org.assertj.core.api.Assertions.assertThat(request.getActionType())
        .isEqualTo(ActivityLogEntry.ActionType.DICE_ROLL);
    org.assertj.core.api.Assertions.assertThat(request.getDescription())
        .isEqualTo("Rolled d20, got 18");
    org.assertj.core.api.Assertions.assertThat(request.getSessionId()).isEqualTo(1L);
    org.assertj.core.api.Assertions.assertThat(request.getCampaignId()).isEqualTo(2L);
    org.assertj.core.api.Assertions.assertThat(request.getUserId()).isEqualTo(3L);
    org.assertj.core.api.Assertions.assertThat(request.getMetadata())
        .containsEntry("sides", 20)
        .containsEntry("result", 18);
  }

  @Test
  void handleActivityEvent_shouldHandleNullMetadata() {
    ActivityEvent event =
        new ActivityEvent(
            ActivityLogEntry.ActionType.SESSION_START,
            "Session 'Adventure' started",
            1L,
            2L,
            null,
            null);

    activityEventListener.handleActivityEvent(event);

    verify(activityLogService).logActivity(any(CreateActivityLogRequest.class));
  }
}
