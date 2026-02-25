package com.rpgmanager.backend.style;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/** Service for managing hierarchical styles (CSS). */
@Service
@RequiredArgsConstructor
@Slf4j
public class StyleService {

  private final StyleRepository styleRepository;
  private final com.rpgmanager.backend.character.domain.repository.CharacterRepository characterRepository;

  /**
   * Retrieves concatenated CSS content for a character.
   */
  @Cacheable(value = "aggregatedStyles", key = "#characterId")
  public String getAggregatedCss(Long characterId) {
    log.info("Aggregating styles for character: {}", characterId);

    return characterRepository.findById(characterId)
        .map(character -> {
          StringBuilder cssBuilder = new StringBuilder();

          // 1. DEFAULT
          appendCssIfPresent(cssBuilder, StyleLevel.DEFAULT, "global");

          // 2. CAMPAIGN
          if (character.getCampaignId() != null) {
            // Base campaign style
            appendCssIfPresent(cssBuilder, StyleLevel.CAMPAIGN, character.getCampaignId().toString());

            // Campaign-specific race style (e.g., "1:Elf")
            if (character.getRace() != null && !character.getRace().isBlank()) {
              appendCssIfPresent(cssBuilder, StyleLevel.CAMPAIGN,
                  character.getCampaignId() + ":" + character.getRace());
            }
          }

          // 3. CHARACTER
          appendCssIfPresent(cssBuilder, StyleLevel.CHARACTER, character.getId().toString());

          if (cssBuilder.isEmpty()) {
            return "/* Default minimal style fallback for " + character.getName()
                + " */\n:root { --race-theme-color: #cccccc; }";
          }

          return cssBuilder.toString();
        })
        .orElse("/* Character " + characterId + " not found */");
  }

  private void appendCssIfPresent(StringBuilder sb, StyleLevel level, String referenceId) {
    Optional<Style> styleOpt = styleRepository.findByLevelAndReferenceId(level, referenceId);
    styleOpt.ifPresent(style -> {
      sb.append("/* --- Level: ").append(level).append(" | Ref: ").append(referenceId).append(" --- */\n");
      sb.append(style.getCssContent()).append("\n\n");
    });
  }

  /**
   * Retrieves a specific style directly.
   */
  @Cacheable(value = "styles", key = "#level + '_' + #referenceId")
  public String getStyle(StyleLevel level, String referenceId) {
    log.info("Fetching CSS style for {} - {} from database", level, referenceId);
    return styleRepository
        .findByLevelAndReferenceId(level, referenceId)
        .map(Style::getCssContent)
        .orElse("");
  }

  /**
   * Saves or updates a generic style.
   */
  @CacheEvict(value = "styles", key = "#level + '_' + #referenceId")
  public Style saveStyle(StyleLevel level, String referenceId, String cssContent) {
    Style style = styleRepository.findByLevelAndReferenceId(level, referenceId).orElse(new Style());
    style.setLevel(level);
    style.setReferenceId(referenceId);
    style.setCssContent(cssContent);
    return styleRepository.save(style);
  }
}
