package com.rpgmanager.backend.style;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/** Service for managing race styles (CSS). */
@Service
@RequiredArgsConstructor
@Slf4j
public class StyleService {

  private final RaceStyleRepository raceStyleRepository;

  /**
   * Retrieves the CSS content for a specific race.
   *
   * @param raceName the name of the race
   * @return the CSS content
   */
  @Cacheable(value = "raceStyles", key = "#raceName")
  public String getCssForRace(String raceName) {
    log.info("Fetching CSS style for race: {} from database", raceName);
    return raceStyleRepository
        .findByRaceName(raceName)
        .map(RaceStyle::getCssContent)
        .orElse("/* Default style for " + raceName + " */\n:root { --race-theme-color: #cccccc; }");
  }

  /**
   * Saves or updates the style for a race.
   *
   * @param raceName the name of the race
   * @param cssContent the CSS content
   * @return the saved RaceStyle entity
   */
  @CacheEvict(value = "raceStyles", key = "#raceName")
  public RaceStyle saveStyle(String raceName, String cssContent) {
    RaceStyle style = raceStyleRepository.findByRaceName(raceName).orElse(new RaceStyle());
    style.setRaceName(raceName);
    style.setCssContent(cssContent);
    return raceStyleRepository.save(style);
  }
}
