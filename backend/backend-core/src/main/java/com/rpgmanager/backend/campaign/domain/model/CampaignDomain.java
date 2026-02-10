package com.rpgmanager.backend.campaign.domain.model;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model representing a Campaign. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDomain {
  private Long id;
  private String name;
  private String description;
  private OffsetDateTime creationDate;
  private CampaignStatus status;
  private Long gameMasterId;
  private String gameMasterName;

  /** Status of the campaign. */
  public enum CampaignStatus {
    ACTIVE,
    FINISHED,
    ARCHIVED
  }
}
