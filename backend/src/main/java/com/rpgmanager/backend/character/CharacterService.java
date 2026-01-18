package com.rpgmanager.backend.character;

import com.rpgmanager.backend.campaign.Campaign;
import com.rpgmanager.backend.campaign.CampaignRepository;
import com.rpgmanager.backend.character.dto.CharacterResponse;
import com.rpgmanager.backend.character.mapper.CharacterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final CampaignRepository campaignRepository;

    @Transactional(readOnly = true)
    public List<CharacterResponse> getAllCharacters() {
        return characterRepository.findAll().stream()
                .map(CharacterMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CharacterResponse updateCharacter(Long id, Character characterDetails) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + id));

        character.setName(characterDetails.getName());
        character.setCharacterClass(characterDetails.getCharacterClass());
        character.setLevel(characterDetails.getLevel());
        character.setStats(characterDetails.getStats());

        Character savedCharacter = characterRepository.save(character);
        return CharacterMapper.toResponse(savedCharacter);
    }

    @Transactional
    public CharacterResponse joinCampaign(Long characterId, Long campaignId) {
        Character character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + characterId));

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));

        character.setCampaign(campaign);
        Character savedCharacter = characterRepository.save(character);
        return CharacterMapper.toResponse(savedCharacter);
    }
}
