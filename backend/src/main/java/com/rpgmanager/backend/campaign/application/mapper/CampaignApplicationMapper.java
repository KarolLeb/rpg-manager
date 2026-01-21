package com.rpgmanager.backend.campaign.application.mapper;

import com.rpgmanager.backend.campaign.application.dto.CampaignDTO;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;

public class CampaignApplicationMapper {

    public static CampaignDTO toDTO(CampaignDomain domain) {
        if (domain == null) {
            return null;
        }
        return CampaignDTO.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .name(domain.getName())
                .description(domain.getDescription())
                .creationDate(domain.getCreationDate())
                .status(domain.getStatus())
                .gameMasterId(domain.getGameMasterId())
                .gameMasterName(domain.getGameMasterName())
                .build();
    }
}
