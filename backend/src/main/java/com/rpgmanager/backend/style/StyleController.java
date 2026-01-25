package com.rpgmanager.backend.style;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing race styles. */
@RestController
@RequestMapping("/api/styles")
@RequiredArgsConstructor
public class StyleController {

  private final StyleService styleService;

  /**
   * Retrieves the CSS for a specific race.
   *
   * @param raceName the name of the race
   * @return the CSS content
   */
  @GetMapping(value = "/{raceName}", produces = "text/css")
  public String getStyle(@PathVariable String raceName) {
    return styleService.getCssForRace(raceName);
  }

  /**
   * Updates the style for a specific race.
   *
   * @param raceName the name of the race
   * @param cssContent the new CSS content
   */
  @PostMapping("/{raceName}")
  public void updateStyle(@PathVariable String raceName, @RequestBody String cssContent) {
    styleService.saveStyle(raceName, cssContent);
  }
}
