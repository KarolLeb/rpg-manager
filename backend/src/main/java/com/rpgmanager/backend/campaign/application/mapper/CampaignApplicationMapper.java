package com.rpgmanager.backend.campaign.application.mapper;

import com.rpgmanager.backend.campaign.application.dto.CampaignDTO;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CampaignApplicationMapper {

  CampaignDTO toDTO(CampaignDomain domain);
}
