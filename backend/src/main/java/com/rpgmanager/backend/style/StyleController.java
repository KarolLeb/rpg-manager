package com.rpgmanager.backend.style;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/styles")
@RequiredArgsConstructor
public class StyleController {

    private final StyleService styleService;

    @GetMapping(value = "/{raceName}", produces = "text/css")
    public String getStyle(@PathVariable String raceName) {
        return styleService.getCssForRace(raceName);
    }

    @PostMapping("/{raceName}")
    public void updateStyle(@PathVariable String raceName, @RequestBody String cssContent) {
        styleService.saveStyle(raceName, cssContent);
    }
}
