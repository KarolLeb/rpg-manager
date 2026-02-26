package com.rpgmanager.backend.actionpolicy.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CharacterActionOverrideTest {

  @Test
  void testLombokMethods() {
    CharacterActionOverride override1 =
        CharacterActionOverride.builder()
            .id(1L)
            .characterId(10L)
            .actionType(ActionType.LEVEL_UP)
            .contextType(ContextType.CAMPAIGN)
            .contextId(100L)
            .isAllowed(true)
            .build();

    CharacterActionOverride override2 =
        new CharacterActionOverride(1L, 10L, ActionType.LEVEL_UP, ContextType.CAMPAIGN, 100L, true);

    assertEquals(override1, override2);
    assertEquals(override1.hashCode(), override2.hashCode());
    assertEquals(1L, override1.getId());
    assertEquals(10L, override1.getCharacterId());
    assertEquals(ActionType.LEVEL_UP, override1.getActionType());
    assertEquals(ContextType.CAMPAIGN, override1.getContextType());
    assertEquals(100L, override1.getContextId());
    assertTrue(override1.isAllowed());
    assertNotNull(override1.toString());

    override1.setAllowed(false);
    assertFalse(override1.isAllowed());

    CharacterActionOverride empty = new CharacterActionOverride();
    assertNull(empty.getId());

    assertNotEquals(new Object(), override1);
    assertNotEquals(null, override1);
  }
}
