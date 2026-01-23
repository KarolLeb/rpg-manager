package com.rpgmanager.backend.style;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StyleService {

  private final RaceStyleRepository raceStyleRepository;

  @Cacheable(value = "raceStyles", key = "#raceName")
  public String getCssForRace(String raceName) {
    log.info("Fetching CSS style for race: {} from database", raceName);
    return raceStyleRepository
        .findByRaceName(raceName)
        .map(RaceStyle::getCssContent)
        .orElse("/* Default style for " + raceName + " */\n:root { --race-theme-color: #cccccc; }");
  }

  @CacheEvict(value = "raceStyles", key = "#raceName")
  public RaceStyle saveStyle(String raceName, String cssContent) {
    RaceStyle style = raceStyleRepository.findByRaceName(raceName).orElse(new RaceStyle());
    style.setRaceName(raceName);
    style.setCssContent(cssContent);
    return raceStyleRepository.save(style);
  }
}
