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
  private Long gameMasterId;
  private String gameMasterName;

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public OffsetDateTime getCreationDate() {
    return creationDate;
  }

  public CampaignStatus getStatus() {
    return status;
  }

  public Long getGameMasterId() {
    return gameMasterId;
  }

  /** Status of the campaign. */
  public enum CampaignStatus {
    ACTIVE,
    FINISHED,
    ARCHIVED
  }
}
