package com.rpgmanager.backend.session;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Data Transfer Object for Session information. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
  private Long id;
  private Long campaignId;
  private String campaignName;
  private String name;
  private String description;
  private OffsetDateTime sessionDate;
  private Session.SessionStatus status;
}
