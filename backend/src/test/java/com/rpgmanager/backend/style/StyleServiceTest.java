package com.rpgmanager.backend.style;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.config.TestContainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestContainersConfig.class)
class StyleServiceTest {

  @Autowired private StyleService styleService;

  @Autowired private RaceStyleRepository raceStyleRepository;

  @Test
  void shouldUpdateCacheWhenStyleIsSaved() {
    String raceName = "TEST_RACE";
    String initialCss = ".test { color: black; }";
    String updatedCss = ".test { color: red; }";

    // 1. Save initial style
    styleService.saveStyle(raceName, initialCss);

    // 2. Fetch to populate cache
    String cachedCss = styleService.getCssForRace(raceName);
    assertThat(cachedCss).isEqualTo(initialCss);

    // 3. Update style
    styleService.saveStyle(raceName, updatedCss);

    // 4. Fetch again - should be updated (fails if cache is not evicted/updated)
    String newCss = styleService.getCssForRace(raceName);
    assertThat(newCss).isEqualTo(updatedCss);
  }
}
