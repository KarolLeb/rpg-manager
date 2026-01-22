package com.rpgmanager.backend.campaign.application.port.in;

import com.rpgmanager.backend.campaign.application.dto.CampaignDTO;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;

public interface GetCampaignUseCase {
    @Cacheable(value = "campaigns")
    List<CampaignDTO> getAllCampaigns();
    CampaignDTO getCampaignById(Long id);
}
