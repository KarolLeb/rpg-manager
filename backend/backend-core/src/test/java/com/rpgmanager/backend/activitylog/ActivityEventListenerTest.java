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

    verify(activityLogService).logActivity(any(CreateActivityLogRequest.class));
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
