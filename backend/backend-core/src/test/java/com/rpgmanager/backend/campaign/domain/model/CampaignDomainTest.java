package com.rpgmanager.backend.campaign.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class CampaignDomainTest {

  @Test
  void testLombokMethods() {
    OffsetDateTime now = OffsetDateTime.now();
    CampaignDomain campaign1 =
        CampaignDomain.builder()
            .id(1L)
            .name("Campaign")
            .description("Desc")
            .creationDate(now)
            .status(CampaignDomain.CampaignStatus.ACTIVE)
            .gameMasterId(10L)
            .gameMasterName("GM")
            .build();

    CampaignDomain campaign2 =
        new CampaignDomain(
            1L, "Campaign", "Desc", now, CampaignDomain.CampaignStatus.ACTIVE, 10L, "GM");

    assertEquals(campaign1, campaign2);
    assertEquals(campaign1.hashCode(), campaign2.hashCode());
    assertEquals("Campaign", campaign1.getName());
    assertEquals(1L, campaign1.getId());
    assertEquals("Desc", campaign1.getDescription());
    assertEquals(now, campaign1.getCreationDate());
    assertEquals(CampaignDomain.CampaignStatus.ACTIVE, campaign1.getStatus());
    assertEquals(10L, campaign1.getGameMasterId());
    assertEquals("GM", campaign1.getGameMasterName());
    assertNotNull(campaign1.toString());

    campaign1.setName("New Name");
    assertEquals("New Name", campaign1.getName());

    CampaignDomain empty = new CampaignDomain();
    assertNull(empty.getId());

    assertNotEquals(campaign1, new Object());
    assertNotEquals(campaign1, null);
  }
}
