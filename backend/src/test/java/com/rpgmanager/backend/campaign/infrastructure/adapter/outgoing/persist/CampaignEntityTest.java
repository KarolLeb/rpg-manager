package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CampaignEntityTest {

  @Test
  void testBuilderAndGetters() {
    CampaignEntity entity =
        CampaignEntity.builder()
            .id(1L)
            .name("Name")
            .description("Desc")
            .gameMasterId(1L)
            .status(CampaignEntity.CampaignStatus.ACTIVE)
            .build();

    assertThat(entity.getId()).isEqualTo(1L);
    assertThat(entity.getName()).isEqualTo("Name");
    assertThat(entity.getDescription()).isEqualTo("Desc");
    assertThat(entity.getGameMasterId()).isEqualTo(1L);
    assertThat(entity.getStatus()).isEqualTo(CampaignEntity.CampaignStatus.ACTIVE);
  }

  @Test
  void testNoArgsConstructor() {
    CampaignEntity entity = new CampaignEntity();
    entity.setName("Test");
    assertThat(entity.getName()).isEqualTo("Test");
  }
}
