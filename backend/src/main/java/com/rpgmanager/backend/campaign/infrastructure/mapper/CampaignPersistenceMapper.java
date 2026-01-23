package com.rpgmanager.backend.campaign.infrastructure.mapper;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.UserEntity;

public class CampaignPersistenceMapper {

  private CampaignPersistenceMapper() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

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
        .status(
            entity.getStatus() != null
                ? CampaignDomain.CampaignStatus.valueOf(entity.getStatus().name())
                : null)
        .gameMasterId(entity.getGameMaster() != null ? entity.getGameMaster().getId() : null)
        .gameMasterName(
            entity.getGameMaster() != null ? entity.getGameMaster().getUsername() : null)
        .build();
  }

  public static CampaignEntity toEntity(CampaignDomain domain, UserEntity gameMaster) {
    if (domain == null) {
      return null;
    }
    return CampaignEntity.builder()
        .id(domain.getId())
        .uuid(domain.getUuid())
        .name(domain.getName())
        .description(domain.getDescription())
        .creationDate(domain.getCreationDate())
        .status(
            domain.getStatus() != null
                ? CampaignEntity.CampaignStatus.valueOf(domain.getStatus().name())
                : null)
        .gameMaster(gameMaster)
        .build();
  }
}
