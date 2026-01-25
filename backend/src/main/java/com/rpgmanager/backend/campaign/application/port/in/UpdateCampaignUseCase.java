package com.rpgmanager.backend.campaign.application.port.in;

import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;

/** Use case for updating existing campaigns. */
public interface UpdateCampaignUseCase {

  /**
   * Updates a campaign.
   *
   * @param id the ID of the campaign to update
   * @param request the update request
   * @return the updated campaign DTO
   */
  CampaignDto updateCampaign(Long id, CreateCampaignRequest request);
}
