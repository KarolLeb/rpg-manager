package com.rpgmanager.backend.character.infrastructure.mapper;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence.CharacterEntity;
import com.rpgmanager.backend.user.User;

public class CharacterPersistenceMapper {

    public static CharacterDomain toDomain(CharacterEntity entity) {
        if (entity == null) {
            return null;
        }
        return CharacterDomain.builder()
                .uuid(entity.getUuid())
                .name(entity.getName())
                .characterClass(entity.getCharacterClass())
                .level(entity.getLevel())
                .stats(entity.getStats())
                .ownerUsername(entity.getUser() != null ? entity.getUser().getUsername() : null)
                .campaignName(entity.getCampaign() != null ? entity.getCampaign().getName() : null)
                .campaignId(entity.getCampaign() != null ? entity.getCampaign().getId() : null)
                .characterType(entity.getCharacterType() != null ? CharacterDomain.CharacterType.valueOf(entity.getCharacterType().name()) : null)
                .build();
    }

    public static CharacterEntity toEntity(CharacterDomain domain, User owner, CampaignEntity campaign) {
        if (domain == null) {
            return null;
        }
        CharacterEntity entity = new CharacterEntity();
        entity.setUuid(domain.getUuid());
        entity.setName(domain.getName());
        entity.setCharacterClass(domain.getCharacterClass());
        entity.setLevel(domain.getLevel());
        entity.setStats(domain.getStats());
        entity.setUser(owner);
        entity.setCampaign(campaign);
        if (domain.getCharacterType() != null) {
            entity.setCharacterType(CharacterEntity.CharacterType.valueOf(domain.getCharacterType().name()));
        }
        return entity;
    }
    
    public static void updateEntity(CharacterEntity entity, CharacterDomain domain, CampaignEntity campaign) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setName(domain.getName());
        entity.setCharacterClass(domain.getCharacterClass());
        entity.setLevel(domain.getLevel());
        entity.setStats(domain.getStats());
        // Only update campaign if provided or logic dictates
        if (campaign != null) {
            entity.setCampaign(campaign);
        }
        if (domain.getCharacterType() != null) {
            entity.setCharacterType(CharacterEntity.CharacterType.valueOf(domain.getCharacterType().name()));
        }
    }
}