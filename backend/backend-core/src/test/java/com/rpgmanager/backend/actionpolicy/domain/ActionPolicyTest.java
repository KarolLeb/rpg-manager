package com.rpgmanager.backend.actionpolicy.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ActionPolicyTest {

  @Test
  void testLombokMethods() {
    ActionPolicy policy1 =
        ActionPolicy.builder()
            .id(1L)
            .actionType(ActionType.LEVEL_UP)
            .contextType(ContextType.CAMPAIGN)
            .contextId(100L)
            .isAllowed(true)
            .build();

    ActionPolicy policy2 =
        new ActionPolicy(1L, ActionType.LEVEL_UP, ContextType.CAMPAIGN, 100L, true);

    assertEquals(policy1, policy2);
    assertEquals(policy1.hashCode(), policy2.hashCode());
    assertEquals(1L, policy1.getId());
    assertEquals(ActionType.LEVEL_UP, policy1.getActionType());
    assertEquals(ContextType.CAMPAIGN, policy1.getContextType());
    assertEquals(100L, policy1.getContextId());
    assertTrue(policy1.isAllowed());
    assertNotNull(policy1.toString());

    policy1.setAllowed(false);
    assertFalse(policy1.isAllowed());

    ActionPolicy empty = new ActionPolicy();
    assertNull(empty.getId());

    assertNotEquals(new Object(), policy1);
    assertNotEquals(null, policy1);
  }
}
