package com.rpgmanager.backend.campaign.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignRequest {
  private String name;
  private String description;
  private Long gameMasterId;
}
