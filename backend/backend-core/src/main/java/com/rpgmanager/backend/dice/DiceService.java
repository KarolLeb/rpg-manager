package com.rpgmanager.backend.dice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/** Service for dice rolling operations. */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiceService {

  private final DiceRollerClient diceRollerClient;
  private final SecureRandom random;

  /**
   * Rolls a die with the specified number of sides using an external API, falling back to a local
   * random generator if the API fails.
   *
   * @param sides the number of sides on the die
   * @return the result of the roll
   */
  @CircuitBreaker(name = "diceRoller", fallbackMethod = "localRoll")
  public int roll(int sides) {
    log.info("Requesting random roll for d{} from external API", sides);
    List<Integer> results = diceRollerClient.rollDice(1, sides, 1);
    return results.get(0);
  }

  /**
   * Fallback method for local dice rolling when the external API is unavailable.
   *
   * @param sides the number of sides on the die
   * @param t the exception that caused the fallback
   * @return the result of the local roll
   */
  public int localRoll(int sides, Throwable t) {
    log.warn("External dice roller failed ({}). Falling back to local roll.", t.getMessage());
    return random.nextInt(sides) + 1;
  }
}
