package com.rpgmanager.backend.style;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing hierarchical CSS styles. */
@RestController
@RequestMapping("/api/styles")
@RequiredArgsConstructor
public class StyleController {

  private final StyleService styleService;

  /**
   * Retrieves aggregated CSS for a character across all matching levels.
   */
  @GetMapping(value = "/aggregated", produces = "text/css")
  public String getAggregatedStyle(@RequestParam Long characterId) {
    return styleService.getAggregatedCss(characterId);
  }

  /**
   * Retrieves the CSS for a specific level and reference.
   */
  @GetMapping(value = "/{level}/{referenceId}", produces = "text/css")
  public String getStyle(
      @PathVariable StyleLevel level, @PathVariable String referenceId) {
    return styleService.getStyle(level, referenceId);
  }

  /**
   * Updates the style for a specific level and reference.
   */
  @PostMapping("/{level}/{referenceId}")
  public void updateStyle(
      @PathVariable StyleLevel level,
      @PathVariable String referenceId,
      @RequestBody String cssContent) {
    styleService.saveStyle(level, referenceId, cssContent);
  }
}
