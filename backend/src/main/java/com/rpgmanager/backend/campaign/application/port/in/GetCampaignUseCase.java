package com.rpgmanager.backend.campaign.application.port.in;

import com.rpgmanager.backend.campaign.application.dto.CampaignDTO;
import java.util.List;

public interface GetCampaignUseCase {
  List<CampaignDTO> getAllCampaigns();

  CampaignDTO getCampaignById(Long id);
}
