package com.rpgmanager.backend.character.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence.CharacterEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class CharacterPersistenceMapperTest {

  @Test
  void toDomain_shouldMapAllFields() {
    CampaignEntity campaign = Instancio.create(CampaignEntity.class);
    CharacterEntity entity = Instancio.create(CharacterEntity.class);
    entity.setUserId(1L);
    entity.setCampaign(campaign);
    entity.setCharacterType(CharacterEntity.CharacterType.PERMANENT);

    CharacterDomain domain = CharacterPersistenceMapper.toDomain(entity);

    assertThat(domain).isNotNull();
    assertThat(domain.getId()).isEqualTo(entity.getId());
    assertThat(domain.getCharacterType().name()).isEqualTo(entity.getCharacterType().name());
    assertThat(domain.getOwnerId()).isEqualTo(1L);
  }

  @Test
  void toEntity_shouldMapAllFields() {
    CampaignEntity campaign = Instancio.create(CampaignEntity.class);
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setOwnerId(1L);
    domain.setCharacterType(CharacterDomain.CharacterType.PERMANENT);

    CharacterEntity entity = CharacterPersistenceMapper.toEntity(domain, campaign);

    assertThat(entity).isNotNull();
    assertThat(entity.getUserId()).isEqualTo(1L);
    assertThat(entity.getCampaign()).isEqualTo(campaign);
  }

  @Test
  void updateEntity_shouldUpdateProvidedFields() {
    CharacterEntity entity = new CharacterEntity();
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setOwnerId(1L);
    domain.setCharacterType(CharacterDomain.CharacterType.TEMPORARY);
    CampaignEntity campaign = Instancio.create(CampaignEntity.class);

    CharacterPersistenceMapper.updateEntity(entity, domain, campaign);

    assertThat(entity.getName()).isEqualTo(domain.getName());
    assertThat(entity.getCampaign()).isEqualTo(campaign);
    assertThat(entity.getUserId()).isEqualTo(1L);
    assertThat(entity.getCharacterType().name()).isEqualTo("TEMPORARY");
  }
}
