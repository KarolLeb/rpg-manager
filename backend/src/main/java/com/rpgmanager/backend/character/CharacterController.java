package com.rpgmanager.backend.character;

import com.rpgmanager.backend.character.dto.CharacterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {
    
    private final CharacterService characterService;

    @GetMapping
    public List<CharacterResponse> getAllCharacters() {
        return characterService.getAllCharacters();
    }

    @GetMapping("/{uuid}")
    public CharacterResponse getCharacter(@PathVariable UUID uuid) {
        return characterService.getCharacter(uuid);
    }

    @PutMapping("/{uuid}")
    public CharacterResponse updateCharacter(@PathVariable UUID uuid, @RequestBody Character characterDetails) {
        return characterService.updateCharacter(uuid, characterDetails);
    }

    @PostMapping("/{uuid}/join-campaign/{campaignId}")
    public CharacterResponse joinCampaign(@PathVariable UUID uuid, @PathVariable Long campaignId) {
        return characterService.joinCampaign(uuid, campaignId);
    }
}
