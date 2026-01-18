package com.rpgmanager.backend.dice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dice")
@RequiredArgsConstructor
public class DiceController {

    private final DiceService diceService;

    @GetMapping("/roll/{sides}")
    public int roll(@PathVariable int sides) {
        return diceService.roll(sides);
    }
}
