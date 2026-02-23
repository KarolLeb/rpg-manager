package com.rpgmanager.backend.activitylog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listens for {@link ActivityEvent} instances published after transaction
 * commit and persists them
 * to the activity log. Runs asynchronously to avoid blocking the originating
 * service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityEventListener {

    private final ActivityLogService activityLogService;

    /**
     * Handles an activity event after the originating transaction commits.
     *
     * @param event the activity event
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleActivityEvent(ActivityEvent event) {
        try {
            CreateActivityLogRequest request = new CreateActivityLogRequest();
            request.setSessionId(event.sessionId());
            request.setCampaignId(event.campaignId());
            request.setUserId(event.userId());
            request.setActionType(event.actionType());
            request.setDescription(event.description());
            request.setMetadata(event.metadata());

            activityLogService.logActivity(request);
            log.debug("Activity logged: {} - {}", event.actionType(), event.description());
        } catch (Exception e) {
            log.error("Failed to log activity event: {}", event, e);
        }
    }
}
