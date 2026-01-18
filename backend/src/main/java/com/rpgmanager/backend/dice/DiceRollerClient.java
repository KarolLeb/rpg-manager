package com.rpgmanager.backend.dice;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "diceRoller", url = "http://www.randomnumberapi.com/api/v1.0")
public interface DiceRollerClient {

    @GetMapping("/random")
    List<Integer> rollDice(
        @RequestParam("min") int min, 
        @RequestParam("max") int max, 
        @RequestParam("count") int count
    );
}
