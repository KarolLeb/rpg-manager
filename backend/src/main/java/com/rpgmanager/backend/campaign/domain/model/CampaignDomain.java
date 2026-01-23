package com.rpgmanager.backend.campaign.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDomain {
  private Long id;
  private UUID uuid;
  private String name;
  private String description;
  private OffsetDateTime creationDate;
  private CampaignStatus status;
  private Long gameMasterId;
  private String gameMasterName;

  public enum CampaignStatus {
    ACTIVE,
    FINISHED,
    ARCHIVED
  }
}
