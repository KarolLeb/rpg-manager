package com.rpgmanager.backend.dice;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "diceRoller", url = "${rpg.dice.url}")
public interface DiceRollerClient {

  @GetMapping("/random")
  List<Integer> rollDice(
      @RequestParam("min") int min, @RequestParam("max") int max, @RequestParam("count") int count);
}
