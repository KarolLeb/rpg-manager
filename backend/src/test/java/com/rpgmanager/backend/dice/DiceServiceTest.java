package com.rpgmanager.backend.dice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DiceServiceTest {

  private DiceService diceService;

  @Mock private DiceRollerClient diceRollerClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    diceService = new DiceService(diceRollerClient);
  }

  @Test
  void shouldRollUsingClient() {
    given(diceRollerClient.rollDice(1, 20, 1)).willReturn(Collections.singletonList(15));

    int result = diceService.roll(20);

    assertEquals(15, result);
  }

  @Test
  void shouldRollLocallyOnFallback() {
    int result = diceService.localRoll(20, new RuntimeException("error"));

    assertTrue(result >= 1 && result <= 20);
  }
}
