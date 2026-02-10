package com.rpgmanager.backend.dice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing dice operations. */
@RestController
@RequestMapping("/api/dice")
@RequiredArgsConstructor
public class DiceController {

  private final DiceService diceService;

  /**
   * Rolls a die with the specified number of sides.
   *
   * @param sides the number of sides on the die
   * @return the result of the roll
   */
  @GetMapping("/roll/{sides}")
  public int roll(@PathVariable int sides) {
    return diceService.roll(sides);
  }
}
