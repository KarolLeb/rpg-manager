package com.rpgmanager.backend.character.infrastructure.adapter.in.web;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.port.in.GetCharacterUseCase;
import com.rpgmanager.backend.character.application.port.in.JoinCampaignUseCase;
import com.rpgmanager.backend.character.application.port.in.UpdateCharacterUseCase;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {
    
    private final GetCharacterUseCase getCharacterUseCase;
    private final UpdateCharacterUseCase updateCharacterUseCase;
    private final JoinCampaignUseCase joinCampaignUseCase;

    @GetMapping
    public List<CharacterResponse> getAllCharacters() {
        return getCharacterUseCase.getAllCharacters();
    }

    @GetMapping("/{uuid}")
    public CharacterResponse getCharacter(@PathVariable UUID uuid) {
        return getCharacterUseCase.getCharacter(uuid);
    }

    @PutMapping("/{uuid}")
    public CharacterResponse updateCharacter(@PathVariable UUID uuid, @RequestBody CharacterDomain characterDetails) {
        // In a real scenario, map a Request DTO to CharacterDomain here
        return updateCharacterUseCase.updateCharacter(uuid, characterDetails);
    }

    @PostMapping("/{uuid}/join-campaign/{campaignId}")
    public CharacterResponse joinCampaign(@PathVariable UUID uuid, @PathVariable Long campaignId) {
        return joinCampaignUseCase.joinCampaign(uuid, campaignId);
    }
}