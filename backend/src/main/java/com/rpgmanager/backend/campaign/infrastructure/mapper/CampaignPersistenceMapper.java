package com.rpgmanager.backend.campaign.infrastructure.mapper;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;

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
        .name(entity.getName())
        .description(entity.getDescription())
        .creationDate(entity.getCreationDate())
        .status(
            entity.getStatus() != null
                ? CampaignDomain.CampaignStatus.valueOf(entity.getStatus().name())
                : null)
        .gameMasterId(entity.getGameMasterId())
        .build();
  }

  public static CampaignEntity toEntity(CampaignDomain domain) {
    if (domain == null) {
      return null;
    }
    return CampaignEntity.builder()
        .id(domain.getId())
        .name(domain.getName())
        .description(domain.getDescription())
        .creationDate(domain.getCreationDate())
        .status(
            domain.getStatus() != null
                ? CampaignEntity.CampaignStatus.valueOf(domain.getStatus().name())
                : null)
        .gameMasterId(domain.getGameMasterId())
        .build();
  }
}
