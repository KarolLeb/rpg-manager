package com.rpgmanager.backend.campaign.infrastructure.mapper;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.user.User;

public class CampaignPersistenceMapper {

    public static CampaignDomain toDomain(CampaignEntity entity) {
        if (entity == null) {
            return null;
        }
        return CampaignDomain.builder()
                .id(entity.getId())
                .uuid(entity.getUuid())
                .name(entity.getName())
                .description(entity.getDescription())
                .creationDate(entity.getCreationDate())
                .status(entity.getStatus() != null ? CampaignDomain.CampaignStatus.valueOf(entity.getStatus().name()) : null)
                .gameMasterId(entity.getGameMaster() != null ? entity.getGameMaster().getId() : null)
                .gameMasterName(entity.getGameMaster() != null ? entity.getGameMaster().getUsername() : null)
                .build();
    }

    public static CampaignEntity toEntity(CampaignDomain domain, User gameMaster) {
        if (domain == null) {
            return null;
        }
        return CampaignEntity.builder()
                .id(domain.getId())
                .uuid(domain.getUuid())
                .name(domain.getName())
                .description(domain.getDescription())
                .creationDate(domain.getCreationDate())
                .status(domain.getStatus() != null ? CampaignEntity.CampaignStatus.valueOf(domain.getStatus().name()) : null)
                .gameMaster(gameMaster)
                .build();
    }
}
