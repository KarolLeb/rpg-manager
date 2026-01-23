package com.rpgmanager.backend.campaign.application.port.in;

import com.rpgmanager.backend.campaign.application.dto.CampaignDTO;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;

public interface CreateCampaignUseCase {
  CampaignDTO createCampaign(CreateCampaignRequest request);
}
