package com.rpgmanager.backend.dice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiceService {

    private final DiceRollerClient diceRollerClient;
    private final SecureRandom random = new SecureRandom();

    @CircuitBreaker(name = "diceRoller", fallbackMethod = "localRoll")
    public int roll(int sides) {
        log.info("Requesting random roll for d{} from external API", sides);
        List<Integer> results = diceRollerClient.rollDice(1, sides, 1);
        return results.get(0);
    }

    public int localRoll(int sides, Throwable t) {
        log.warn("External dice roller failed ({}). Falling back to local roll.", t.getMessage());
        return random.nextInt(sides) + 1;
    }
}
