package com.rpgmanager.backend.character;

import com.rpgmanager.backend.campaign.Campaign;
import com.rpgmanager.backend.campaign.CampaignRepository;
import com.rpgmanager.backend.character.dto.CharacterResponse;
import com.rpgmanager.backend.character.mapper.CharacterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
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
    public CharacterResponse getCharacter(UUID uuid) {
        Character character = characterRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Character not found with uuid: " + uuid));
        return CharacterMapper.toResponse(character);
    }

    @Transactional
    public CharacterResponse updateCharacter(UUID uuid, Character characterDetails) {
        Character character = characterRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Character not found with uuid: " + uuid));

        character.setName(characterDetails.getName());
        character.setCharacterClass(characterDetails.getCharacterClass());
        character.setLevel(characterDetails.getLevel());
        character.setStats(characterDetails.getStats());

        Character savedCharacter = characterRepository.save(character);
        return CharacterMapper.toResponse(savedCharacter);
    }

    @Transactional
    public CharacterResponse joinCampaign(UUID characterUuid, Long campaignId) {
        Character character = characterRepository.findByUuid(characterUuid)
                .orElseThrow(() -> new RuntimeException("Character not found with uuid: " + characterUuid));

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + campaignId));

        character.setCampaign(campaign);
        Character savedCharacter = characterRepository.save(character);
        return CharacterMapper.toResponse(savedCharacter);
    }
}
