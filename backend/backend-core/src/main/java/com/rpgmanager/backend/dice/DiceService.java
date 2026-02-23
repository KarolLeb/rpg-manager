package com.rpgmanager.backend.dice;

import com.rpgmanager.backend.activitylog.ActivityEvent;
import com.rpgmanager.backend.activitylog.ActivityLogEntry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/** Service for dice rolling operations. */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiceService {

  private final DiceRollerClient diceRollerClient;
  private final SecureRandom random;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Rolls a die with the specified number of sides using an external API, falling
   * back to a local
   * random generator if the API fails.
   *
   * @param sides the number of sides on the die
   * @return the result of the roll
   */
  @CircuitBreaker(name = "diceRoller", fallbackMethod = "localRoll")
  public int roll(int sides) {
    log.info("Requesting random roll for d{} from external API", sides);
    List<Integer> results = diceRollerClient.rollDice(1, sides, 1);
    int result = results.get(0);
    publishDiceRollEvent(sides, result);
    return result;
  }

  /**
   * Fallback method for local dice rolling when the external API is unavailable.
   *
   * @param sides the number of sides on the die
   * @param t     the exception that caused the fallback
   * @return the result of the local roll
   */
  public int localRoll(int sides, Throwable t) {
    log.warn("External dice roller failed ({}). Falling back to local roll.", t.getMessage());
    int result = random.nextInt(sides) + 1;
    publishDiceRollEvent(sides, result);
    return result;
  }

  private void publishDiceRollEvent(int sides, int result) {
    eventPublisher.publishEvent(
        new ActivityEvent(
            ActivityLogEntry.ActionType.DICE_ROLL,
            String.format("Rolled d%d, got %d", sides, result),
            null,
            null,
            null,
            Map.of("sides", sides, "result", result)));
  }
}
