package com.rpgmanager.backend.dice;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Feign client for the external Dice Roller API. */
@FeignClient(name = "diceRoller", url = "${rpg.dice.url}")
public interface DiceRollerClient {

  /**
   * Rolls a set of dice.
   *
   * @param min the minimum value
   * @param max the maximum value (number of sides)
   * @param count the number of dice to roll
   * @return a list of roll results
   */
  @GetMapping("/random")
  List<Integer> rollDice(
      @RequestParam("min") int min, @RequestParam("max") int max, @RequestParam("count") int count);
}
