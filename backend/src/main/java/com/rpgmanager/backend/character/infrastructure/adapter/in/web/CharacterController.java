package com.rpgmanager.backend.character.infrastructure.adapter.in.web;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.port.in.GetCharacterUseCase;
import com.rpgmanager.backend.character.application.port.in.JoinCampaignUseCase;
import com.rpgmanager.backend.character.application.port.in.UpdateCharacterUseCase;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/{id}")
  public CharacterResponse getCharacter(@PathVariable Long id) {
    return getCharacterUseCase.getCharacter(id);
  }

  @PutMapping("/{id}")
  public CharacterResponse updateCharacter(
      @PathVariable Long id, @RequestBody CharacterDomain characterDetails) {
    // In a real scenario, map a Request DTO to CharacterDomain here
    return updateCharacterUseCase.updateCharacter(id, characterDetails);
  }

  @PostMapping("/{id}/join-campaign/{campaignId}")
  public CharacterResponse joinCampaign(@PathVariable Long id, @PathVariable Long campaignId) {
    return joinCampaignUseCase.joinCampaign(id, campaignId);
  }
}
