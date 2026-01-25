package com.rpgmanager.backend.character.infrastructure.mapper;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence.CharacterEntity;

public class CharacterPersistenceMapper {

  private CharacterPersistenceMapper() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static CharacterDomain toDomain(CharacterEntity entity) {
    if (entity == null) {
      return null;
    }
    return CharacterDomain.builder()
        .id(entity.getId())
        .name(entity.getName())
        .characterClass(entity.getCharacterClass())
        .level(entity.getLevel())
        .stats(entity.getStats())
        .ownerId(entity.getUserId())
        .controllerId(entity.getControllerId())
        .campaignName(entity.getCampaign() != null ? entity.getCampaign().getName() : null)
        .campaignId(entity.getCampaign() != null ? entity.getCampaign().getId() : null)
        .characterType(
            entity.getCharacterType() != null
                ? CharacterDomain.CharacterType.valueOf(entity.getCharacterType().name())
                : null)
        .build();
  }

  public static CharacterEntity toEntity(CharacterDomain domain, CampaignEntity campaign) {
    if (domain == null) {
      return null;
    }
    CharacterEntity entity = new CharacterEntity();
    entity.setName(domain.getName());
    entity.setCharacterClass(domain.getCharacterClass());
    entity.setLevel(domain.getLevel());
    entity.setStats(domain.getStats());
    entity.setUserId(domain.getOwnerId());
    entity.setControllerId(domain.getControllerId());
    entity.setCampaign(campaign);
    if (domain.getCharacterType() != null) {
      entity.setCharacterType(
          CharacterEntity.CharacterType.valueOf(domain.getCharacterType().name()));
    }
    return entity;
  }

  public static void updateEntity(
      CharacterEntity entity, CharacterDomain domain, CampaignEntity campaign) {
    if (domain == null || entity == null) {
      return;
    }
    entity.setName(domain.getName());
    entity.setCharacterClass(domain.getCharacterClass());
    entity.setLevel(domain.getLevel());
    entity.setStats(domain.getStats());
    entity.setUserId(domain.getOwnerId());
    entity.setControllerId(domain.getControllerId());
    // Only update campaign if provided or logic dictates
    if (campaign != null) {
      entity.setCampaign(campaign);
    }
    if (domain.getCharacterType() != null) {
      entity.setCharacterType(
          CharacterEntity.CharacterType.valueOf(domain.getCharacterType().name()));
    }
  }
}
