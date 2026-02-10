package com.rpgmanager.backend.campaign.application.port.in;

import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import java.util.List;

/** Use case for retrieving campaign information. */
public interface GetCampaignUseCase {

  /**
   * Retrieves all campaigns.
   *
   * @return a list of all campaign DTOs
   */
  List<CampaignDto> getAllCampaigns();

  /**
   * Retrieves a specific campaign by ID.
   *
   * @param id the campaign ID
   * @return the campaign DTO
   */
  CampaignDto getCampaignById(Long id);
}
