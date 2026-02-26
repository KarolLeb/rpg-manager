package com.rpgmanager.backend.character.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CharacterDomainTest {

  @Test
  void testLombokMethods() {
    CharacterDomain char1 =
        CharacterDomain.builder()
            .id(1L)
            .name("Char")
            .race("Human")
            .characterClass("Warrior")
            .level(5)
            .stats("Stats")
            .ownerId(100L)
            .ownerUsername("Owner")
            .controllerId(101L)
            .campaignName("Campaign")
            .campaignId(200L)
            .characterType(CharacterDomain.CharacterType.PERMANENT)
            .build();

    CharacterDomain char2 =
        new CharacterDomain(
            1L,
            "Char",
            "Human",
            "Warrior",
            5,
            "Stats",
            100L,
            "Owner",
            101L,
            "Campaign",
            200L,
            CharacterDomain.CharacterType.PERMANENT);

    assertEquals(char1, char2);
    assertEquals(char1.hashCode(), char2.hashCode());
    assertEquals("Char", char1.getName());
    assertEquals("Human", char1.getRace());
    assertEquals("Warrior", char1.getCharacterClass());
    assertEquals(5, char1.getLevel());
    assertEquals("Stats", char1.getStats());
    assertEquals(100L, char1.getOwnerId());
    assertEquals("Owner", char1.getOwnerUsername());
    assertEquals(101L, char1.getControllerId());
    assertEquals("Campaign", char1.getCampaignName());
    assertEquals(200L, char1.getCampaignId());
    assertEquals(CharacterDomain.CharacterType.PERMANENT, char1.getCharacterType());
    assertNotNull(char1.toString());

    char1.setName("New Name");
    assertEquals("New Name", char1.getName());

    CharacterDomain empty = new CharacterDomain();
    assertNull(empty.getId());

    assertNotEquals(char1, new Object());
    assertNotEquals(char1, null);
  }
}
