package com.rpgmanager.backend.campaign.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;

class CampaignPersistenceMapperTest {

  @Test
  void toDomain_shouldMapAllFields() {
    CampaignEntity entity = Instancio.create(CampaignEntity.class);
    entity.setGameMasterId(1L);
    entity.setStatus(CampaignEntity.CampaignStatus.ACTIVE);

    CampaignDomain domain = CampaignPersistenceMapper.toDomain(entity);

    assertThat(domain).isNotNull();
    assertThat(domain.getId()).isEqualTo(entity.getId());
    assertThat(domain.getName()).isEqualTo(entity.getName());
    assertThat(domain.getStatus().name()).isEqualTo(entity.getStatus().name());
    assertThat(domain.getGameMasterId()).isEqualTo(1L);
  }

  @Test
  void toDomain_shouldReturnNull_whenEntityIsNull() {
    assertThat(CampaignPersistenceMapper.toDomain(null)).isNull();
  }

  @Test
  void toEntity_shouldMapAllFields() {
    CampaignDomain domain = Instancio.create(CampaignDomain.class);
    domain.setGameMasterId(1L);
    domain.setStatus(CampaignDomain.CampaignStatus.ACTIVE);

    CampaignEntity entity = CampaignPersistenceMapper.toEntity(domain);

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(domain.getId());
    assertThat(entity.getName()).isEqualTo(domain.getName());
    assertThat(entity.getStatus().name()).isEqualTo(domain.getStatus().name());
    assertThat(entity.getGameMasterId()).isEqualTo(1L);
  }

  @Test
  void toEntity_shouldReturnNull_whenDomainIsNull() {
    assertThat(CampaignPersistenceMapper.toEntity(null)).isNull();
  }
}
