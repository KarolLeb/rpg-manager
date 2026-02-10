package com.rpgmanager.backend.style;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StyleServiceTest extends BaseIntegrationTest {

  @Autowired private StyleService styleService;

  @Autowired private RaceStyleRepository raceStyleRepository;

  @Test
  void shouldUpdateCacheWhenStyleIsSaved() {
    String raceName = "TEST_RACE";
    String initialCss = ".test { color: black; }";
    String updatedCss = ".test { color: red; }";

    // 1. Save initial style
    RaceStyle saved = styleService.saveStyle(raceName, initialCss);
    assertThat(saved).isNotNull().extracting(RaceStyle::getRaceName).isEqualTo(raceName);

    // 2. Fetch to populate cache
    assertThat(styleService.getCssForRace(raceName)).isEqualTo(initialCss);

    // 3. Update style
    styleService.saveStyle(raceName, updatedCss);

    // 4. Fetch again - should be updated
    assertThat(styleService.getCssForRace(raceName)).isEqualTo(updatedCss);
  }

  @Test
  void shouldReturnDefaultCss_whenStyleNotFound() {
    String raceName = "UNKNOWN_RACE";

    assertThat(styleService.getCssForRace(raceName))
        .contains("Default style for UNKNOWN_RACE")
        .contains("--race-theme-color: #cccccc;");
  }
}
