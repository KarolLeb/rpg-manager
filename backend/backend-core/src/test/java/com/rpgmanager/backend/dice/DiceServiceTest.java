package com.rpgmanager.backend.dice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import java.security.SecureRandom;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DiceServiceTest {

  private DiceService diceService;

  @Mock private DiceRollerClient diceRollerClient;
  @Mock private SecureRandom secureRandom;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    diceService = new DiceService(diceRollerClient, secureRandom);
  }

  @Test
  void shouldRollUsingClient() {
    given(diceRollerClient.rollDice(1, 20, 1)).willReturn(Collections.singletonList(15));

    int result = diceService.roll(20);

    assertEquals(15, result);
  }

  @Test
  void shouldRollLocallyOnFallback() {
    given(secureRandom.nextInt(20)).willReturn(5);

    int result = diceService.localRoll(20, new RuntimeException("error"));

    assertEquals(6, result);
  }
}
