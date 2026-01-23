package com.rpgmanager.backend.character.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence.CharacterEntity;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.UserEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class CharacterPersistenceMapperTest {

  @Test
  void toDomain_shouldMapAllFields() {
    UserEntity user = Instancio.create(UserEntity.class);
    CampaignEntity campaign = Instancio.create(CampaignEntity.class);
    CharacterEntity entity = Instancio.create(CharacterEntity.class);
    entity.setUser(user);
    entity.setCampaign(campaign);
    entity.setCharacterType(CharacterEntity.CharacterType.PERMANENT);

    CharacterDomain domain = CharacterPersistenceMapper.toDomain(entity);

    assertThat(domain).isNotNull();
    assertThat(domain.getUuid()).isEqualTo(entity.getUuid());
    assertThat(domain.getCharacterType().name()).isEqualTo(entity.getCharacterType().name());
  }

  @Test
  void toEntity_shouldMapAllFields() {
    UserEntity user = Instancio.create(UserEntity.class);
    CampaignEntity campaign = Instancio.create(CampaignEntity.class);
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setCharacterType(CharacterDomain.CharacterType.PERMANENT);

    CharacterEntity entity = CharacterPersistenceMapper.toEntity(domain, user, campaign);

    assertThat(entity).isNotNull();
    assertThat(entity.getUuid()).isEqualTo(domain.getUuid());
    assertThat(entity.getUser()).isEqualTo(user);
    assertThat(entity.getCampaign()).isEqualTo(campaign);
  }

  @Test
  void updateEntity_shouldUpdateProvidedFields() {
    CharacterEntity entity = new CharacterEntity();
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setCharacterType(CharacterDomain.CharacterType.TEMPORARY);
    CampaignEntity campaign = Instancio.create(CampaignEntity.class);

    CharacterPersistenceMapper.updateEntity(entity, domain, campaign);

    assertThat(entity.getName()).isEqualTo(domain.getName());
    assertThat(entity.getCampaign()).isEqualTo(campaign);
    assertThat(entity.getCharacterType().name()).isEqualTo("TEMPORARY");
  }
}
