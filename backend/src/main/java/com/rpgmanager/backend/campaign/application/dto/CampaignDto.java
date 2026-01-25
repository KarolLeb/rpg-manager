package com.rpgmanager.backend.campaign.application.dto;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Data Transfer Object for Campaign information. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDto implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long id;
  private String name;
  private String description;
  private OffsetDateTime creationDate;
  private CampaignDomain.CampaignStatus status;
  private Long gameMasterId;
  private String gameMasterName;
}
