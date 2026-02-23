package com.rpgmanager.backend.activitylog;

import java.time.OffsetDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for activity log responses. Excludes the raw embedding vector. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogDto {

  private Long id;
  private Long sessionId;
  private Long campaignId;
  private Long userId;
  private ActivityLogEntry.ActionType actionType;
  private String description;
  private Map<String, Object> metadata;
  private OffsetDateTime createdAt;
  private Double similarityScore;
}
