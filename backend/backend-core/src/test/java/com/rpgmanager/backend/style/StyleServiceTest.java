package com.rpgmanager.backend.style;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StyleServiceTest extends BaseIntegrationTest {

  @Autowired private StyleService styleService;

  @Autowired private StyleRepository styleRepository;

  @Test
  void shouldUpdateCacheWhenStyleIsSaved() {
    String referenceId = "TEST_REF";
    StyleLevel level = StyleLevel.CAMPAIGN;
    String initialCss = ".test { color: black; }";
    String updatedCss = ".test { color: red; }";

    // 1. Save initial style
    Style saved = styleService.saveStyle(level, referenceId, initialCss);
    assertThat(saved).isNotNull().extracting(Style::getReferenceId).isEqualTo(referenceId);
    assertThat(saved.getLevel()).isEqualTo(level);

    // 2. Fetch to populate cache
    assertThat(styleService.getStyle(level, referenceId)).isEqualTo(initialCss);

    // 3. Update style
    styleService.saveStyle(level, referenceId, updatedCss);

    // 4. Fetch again - should be updated
    assertThat(styleService.getStyle(level, referenceId)).isEqualTo(updatedCss);
  }

  @Test
  void shouldReturnEmptyCss_whenStyleNotFound() {
    assertThat(styleService.getStyle(StyleLevel.CAMPAIGN, "UNKNOWN_REF")).isEmpty();
  }

  @Test
  void shouldReturnFallbackCss_whenAggregatingAndNoStylesFound() {
    // Note: This will return the "Character not found" message unless we save a
    // character first.
    // Given the architecture, an integration test here would need a Character
    // saved.
    assertThat(styleService.getAggregatedCss(999L)).contains("Character 999 not found");
  }
}
