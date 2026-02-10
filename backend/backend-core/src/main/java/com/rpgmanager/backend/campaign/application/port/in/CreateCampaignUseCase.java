package com.rpgmanager.backend.campaign.application.port.in;

import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;

/** Use case for creating a new campaign. */
public interface CreateCampaignUseCase {

  /**
   * Creates a new campaign.
   *
   * @param request the campaign creation request
   * @return the created campaign DTO
   */
  CampaignDto createCampaign(CreateCampaignRequest request);
}
