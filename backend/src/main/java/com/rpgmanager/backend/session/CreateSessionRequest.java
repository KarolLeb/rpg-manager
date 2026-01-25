package com.rpgmanager.backend.session;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Request object for creating a new game session. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSessionRequest {
  private Long campaignId;
  private String name;
  private String description;
  private OffsetDateTime sessionDate;
}
