package com.rpgmanager.backend.session;

import com.rpgmanager.backend.session.Session.SessionStatus;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionDTO {
  private Long id;
  private Long campaignId;
  private String campaignName;
  private String name;
  private String description;
  private OffsetDateTime sessionDate;
  private SessionStatus status;
}
