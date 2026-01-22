package com.rpgmanager.backend.character.application.service;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.mapper.CharacterApplicationMapper;
import com.rpgmanager.backend.character.application.port.in.GetCharacterUseCase;
import com.rpgmanager.backend.character.application.port.in.JoinCampaignUseCase;
import com.rpgmanager.backend.character.application.port.in.UpdateCharacterUseCase;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.domain.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterApplicationService implements GetCharacterUseCase, UpdateCharacterUseCase, JoinCampaignUseCase {

    private final CharacterRepository characterRepository;
    private final CharacterApplicationMapper characterApplicationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CharacterResponse> getAllCharacters() {
        return characterRepository.findAll().stream()
                .map(characterApplicationMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterResponse getCharacter(UUID uuid) {
        CharacterDomain character = characterRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Character not found with uuid: " + uuid));
        return characterApplicationMapper.toResponse(character);
    }

    @Override
    @Transactional
    public CharacterResponse updateCharacter(UUID uuid, CharacterDomain characterDetails) {
        CharacterDomain character = characterRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Character not found with uuid: " + uuid));

        character.setName(characterDetails.getName());
        character.setCharacterClass(characterDetails.getCharacterClass());
        character.setLevel(characterDetails.getLevel());
        character.setStats(characterDetails.getStats());

        CharacterDomain savedCharacter = characterRepository.save(character);
        return characterApplicationMapper.toResponse(savedCharacter);
    }

    @Override
    @Transactional
    public CharacterResponse joinCampaign(UUID characterUuid, Long campaignId) {
        CharacterDomain character = characterRepository.findByUuid(characterUuid)
                .orElseThrow(() -> new RuntimeException("Character not found with uuid: " + characterUuid));

        // Note: Campaign verification should ideally happen here via a CampaignPort
        // For now, we trust the ID or handle it in the persistence adapter
        character.setCampaignId(campaignId);
        
        CharacterDomain savedCharacter = characterRepository.save(character);
        return characterApplicationMapper.toResponse(savedCharacter);
    }
}
