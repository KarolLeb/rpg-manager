package com.rpgmanager.backend.character.application.service;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.mapper.CharacterApplicationMapper;
import com.rpgmanager.backend.character.application.port.in.GetCharacterUseCase;
import com.rpgmanager.backend.character.application.port.in.JoinCampaignUseCase;
import com.rpgmanager.backend.character.application.port.in.UpdateCharacterUseCase;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.domain.repository.CharacterRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CharacterApplicationService
    implements GetCharacterUseCase, UpdateCharacterUseCase, JoinCampaignUseCase {

  private static final String CHARACTER_NOT_FOUND_MSG = "Character not found with id: ";
  private final CharacterRepository characterRepository;
  private final CharacterApplicationMapper characterApplicationMapper;

  @Override
  @Transactional(readOnly = true)
  public List<CharacterResponse> getAllCharacters() {
    return characterRepository.findAll().stream()
        .map(characterApplicationMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CharacterResponse getCharacter(Long id) {
    CharacterDomain character =
        characterRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException(CHARACTER_NOT_FOUND_MSG + id));
    return characterApplicationMapper.toResponse(character);
  }

  @Override
  @Transactional
  public CharacterResponse updateCharacter(Long id, CharacterDomain characterDetails) {
    CharacterDomain character =
        characterRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException(CHARACTER_NOT_FOUND_MSG + id));

    character.setName(characterDetails.getName());
    character.setCharacterClass(characterDetails.getCharacterClass());
    character.setLevel(characterDetails.getLevel());
    character.setStats(characterDetails.getStats());

    CharacterDomain savedCharacter = characterRepository.save(character);
    return characterApplicationMapper.toResponse(savedCharacter);
  }

  @Override
  @Transactional
  public CharacterResponse joinCampaign(Long characterId, Long campaignId) {
    CharacterDomain character =
        characterRepository
            .findById(characterId)
            .orElseThrow(() -> new IllegalArgumentException(CHARACTER_NOT_FOUND_MSG + characterId));

    // Note: Campaign verification should ideally happen here via a CampaignPort
    // For now, we trust the ID or handle it in the persistence adapter
    character.setCampaignId(campaignId);

    CharacterDomain savedCharacter = characterRepository.save(character);
    return characterApplicationMapper.toResponse(savedCharacter);
  }
}
