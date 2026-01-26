package com.rpgmanager.backend.character.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence.CharacterEntity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class CharacterPersistenceMapperTest {

  @Test
  void constructor_shouldThrowException() throws Exception {
    Constructor<CharacterPersistenceMapper> constructor =
        CharacterPersistenceMapper.class.getDeclaredConstructor();
    constructor.setAccessible(true);
    InvocationTargetException exception =
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    assertThat(exception.getCause()).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void toDomain_shouldHandleNull() {
    assertThat(CharacterPersistenceMapper.toDomain(null)).isNull();
  }

  @Test
  void toDomain_shouldHandleNullCampaignAndType() {
    CharacterEntity entity = new CharacterEntity();
    entity.setCampaign(null);
    entity.setCharacterType(null);

    CharacterDomain domain = CharacterPersistenceMapper.toDomain(entity);

    assertThat(domain.getCampaignName()).isNull();
    assertThat(domain.getCharacterType()).isNull();
  }

  @Test
  void toEntity_shouldHandleNull() {
    assertThat(CharacterPersistenceMapper.toEntity(null, null)).isNull();
  }

  @Test
  void toEntity_shouldHandleNullType() {
    CharacterDomain domain = CharacterDomain.builder().characterType(null).build();
    CharacterEntity entity = CharacterPersistenceMapper.toEntity(domain, null);
    assertThat(entity.getCharacterType()).isEqualTo(CharacterEntity.CharacterType.PERMANENT);
  }

  @Test
  void updateEntity_shouldHandleNull() {
    org.junit.jupiter.api.Assertions.assertDoesNotThrow(
        () -> CharacterPersistenceMapper.updateEntity(null, null, null));
  }

  @Test
  void updateEntity_shouldHandleNullCampaign() {
    CharacterEntity entity = new CharacterEntity();
    CharacterDomain domain = CharacterDomain.builder().name("New").build();
    CharacterPersistenceMapper.updateEntity(entity, domain, null);
    assertThat(entity.getName()).isEqualTo("New");
    assertThat(entity.getCampaign()).isNull();
  }

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
    assertThat(domain.getControllerId()).isEqualTo(entity.getControllerId());
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
    assertThat(entity.getControllerId()).isEqualTo(domain.getControllerId());
    assertThat(entity.getCharacterType().name()).isEqualTo(domain.getCharacterType().name());
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
    assertThat(entity.getControllerId()).isEqualTo(domain.getControllerId());
    assertThat(entity.getCharacterType().name()).isEqualTo("TEMPORARY");
  }
}
