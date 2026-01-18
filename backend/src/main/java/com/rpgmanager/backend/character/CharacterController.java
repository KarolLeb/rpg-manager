package com.rpgmanager.backend.character;

import com.rpgmanager.backend.character.dto.CharacterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {
    
    private final CharacterService characterService;

    @GetMapping
    public List<CharacterResponse> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    @PutMapping("/{id}")
    public CharacterResponse updateCharacter(@PathVariable Long id, @RequestBody Character characterDetails) {
        return characterService.updateCharacter(id, characterDetails);
    }

    @PostMapping("/{id}/join-campaign/{campaignId}")
    public CharacterResponse joinCampaign(@PathVariable Long id, @PathVariable Long campaignId) {
        return characterService.joinCampaign(id, campaignId);
    }
}
