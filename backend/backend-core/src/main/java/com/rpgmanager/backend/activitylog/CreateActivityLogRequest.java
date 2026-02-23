package com.rpgmanager.backend.activitylog;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request DTO for creating a new activity log entry. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityLogRequest {

  private Long sessionId;
  private Long campaignId;
  private Long userId;
  private ActivityLogEntry.ActionType actionType;
  private String description;
  private Map<String, Object> metadata;
}
