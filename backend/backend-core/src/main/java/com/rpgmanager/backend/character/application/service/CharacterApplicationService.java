package com.rpgmanager.backend.character.application.service;

import com.rpgmanager.backend.activitylog.ActivityEvent;
import com.rpgmanager.backend.activitylog.ActivityLogEntry;
import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.mapper.CharacterApplicationMapper;
import com.rpgmanager.backend.character.application.port.in.GetCharacterUseCase;
import com.rpgmanager.backend.character.application.port.in.JoinCampaignUseCase;
import com.rpgmanager.backend.character.application.port.in.UpdateCharacterUseCase;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.domain.repository.CharacterRepository;
import com.rpgmanager.common.security.UserContext;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service implementation for Character application use cases. */
@Service
@RequiredArgsConstructor
public class CharacterApplicationService
    implements GetCharacterUseCase, UpdateCharacterUseCase, JoinCampaignUseCase {

  private static final String CHARACTER_NOT_FOUND_MSG = "Character not found with id: ";
  private final CharacterRepository characterRepository;
  private final CharacterApplicationMapper characterApplicationMapper;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Retrieves all characters.
   *
   * @return a list of all character responses
   */
  @Override
  @Transactional(readOnly = true)
  public List<CharacterResponse> getAllCharacters() {
    return characterRepository.findAll().stream()
        .map(characterApplicationMapper::toResponse)
        .toList();
  }

  /**
   * Retrieves a specific character by ID.
   *
   * @param id the character ID
   * @return the character response
   */
  @Override
  @Transactional(readOnly = true)
  public CharacterResponse getCharacter(Long id) {
    CharacterDomain character =
        characterRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException(CHARACTER_NOT_FOUND_MSG + id));
    return characterApplicationMapper.toResponse(character);
  }

  /**
   * Updates an existing character.
   *
   * @param id the ID of the character to update
   * @param characterDetails the new character details
   * @return the updated character response
   */
  @Override
  @Transactional
  public CharacterResponse updateCharacter(Long id, CharacterDomain characterDetails) {
    CharacterDomain character =
        characterRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException(CHARACTER_NOT_FOUND_MSG + id));

    checkCharacterAccess(character);

    character.setName(characterDetails.getName());
    character.setCharacterClass(characterDetails.getCharacterClass());
    character.setLevel(characterDetails.getLevel());
    character.setStats(characterDetails.getStats());

    CharacterDomain savedCharacter = characterRepository.save(character);
    eventPublisher.publishEvent(
        new ActivityEvent(
            ActivityLogEntry.ActionType.CHARACTER_CHANGE,
            String.format("Character '%s' updated", savedCharacter.getName()),
            null,
            savedCharacter.getCampaignId(),
            null,
            Map.of(
                "characterId", savedCharacter.getId(), "characterName", savedCharacter.getName())));
    return characterApplicationMapper.toResponse(savedCharacter);
  }

  /**
   * Allows a character to join a campaign.
   *
   * @param characterId the ID of the character
   * @param campaignId the ID of the campaign
   * @return the updated character response
   */
  @Override
  @Transactional
  public CharacterResponse joinCampaign(Long characterId, Long campaignId) {
    CharacterDomain character =
        characterRepository
            .findById(characterId)
            .orElseThrow(() -> new IllegalArgumentException(CHARACTER_NOT_FOUND_MSG + characterId));

    checkCharacterAccess(character);

    // Note: Campaign verification should ideally happen here via a CampaignPort
    // For now, we trust the ID or handle it in the persistence adapter
    character.setCampaignId(campaignId);

    CharacterDomain savedCharacter = characterRepository.save(character);
    eventPublisher.publishEvent(
        new ActivityEvent(
            ActivityLogEntry.ActionType.CHARACTER_CHANGE,
            String.format(
                "Character '%s' joined campaign %d", savedCharacter.getName(), campaignId),
            null,
            campaignId,
            null,
            Map.of("characterId", savedCharacter.getId(), "campaignId", campaignId)));
    return characterApplicationMapper.toResponse(savedCharacter);
  }

  private void checkCharacterAccess(CharacterDomain character) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserContext userContext) {
      boolean isAdmin =
          userContext.getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

      boolean isOwner = userContext.getUserId().equals(character.getOwnerId());
      boolean isController = userContext.getUserId().equals(character.getControllerId());

      if (!isAdmin && !isOwner && !isController) {
        throw new AccessDeniedException("You do not have permission to modify this character.");
      }
    } else {
      throw new AccessDeniedException("User not authenticated.");
    }
  }
}
