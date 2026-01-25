package com.rpgmanager.backend.character.infrastructure.adapter.in.web;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.port.in.GetCharacterUseCase;
import com.rpgmanager.backend.character.application.port.in.JoinCampaignUseCase;
import com.rpgmanager.backend.character.application.port.in.UpdateCharacterUseCase;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing character operations. */
@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

  private final GetCharacterUseCase getCharacterUseCase;
  private final UpdateCharacterUseCase updateCharacterUseCase;
  private final JoinCampaignUseCase joinCampaignUseCase;

  /**
   * Retrieves all characters.
   *
   * @return a list of all characters
   */
  @GetMapping
  public List<CharacterResponse> getAllCharacters() {
    return getCharacterUseCase.getAllCharacters();
  }

  /**
   * Retrieves a specific character by ID.
   *
   * @param id the ID of the character to retrieve
   * @return the character details
   */
  @GetMapping("/{id}")
  public CharacterResponse getCharacter(@PathVariable Long id) {
    return getCharacterUseCase.getCharacter(id);
  }

  /**
   * Updates an existing character.
   *
   * @param id the ID of the character to update
   * @param characterDetails the new character details
   * @return the updated character response
   */
  @PutMapping("/{id}")
  public CharacterResponse updateCharacter(
      @PathVariable Long id, @RequestBody CharacterDomain characterDetails) {
    // In a real scenario, map a Request DTO to CharacterDomain here
    return updateCharacterUseCase.updateCharacter(id, characterDetails);
  }

  /**
   * Allows a character to join a campaign.
   *
   * @param id the ID of the character
   * @param campaignId the ID of the campaign to join
   * @return the updated character response
   */
  @PostMapping("/{id}/join-campaign/{campaignId}")
  public CharacterResponse joinCampaign(@PathVariable Long id, @PathVariable Long campaignId) {
    return joinCampaignUseCase.joinCampaign(id, campaignId);
  }
}
