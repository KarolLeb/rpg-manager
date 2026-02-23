package com.rpgmanager.backend.activitylog;

import java.util.Map;

/**
 * Application event representing a user action that should be recorded in the activity log. Used by
 * services to publish activity events without coupling directly to the activity log infrastructure.
 */
public record ActivityEvent(
    ActivityLogEntry.ActionType actionType,
    String description,
    Long sessionId,
    Long campaignId,
    Long userId,
    Map<String, Object> metadata) {}
