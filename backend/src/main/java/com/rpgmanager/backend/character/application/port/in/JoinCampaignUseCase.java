package com.rpgmanager.backend.character.application.port.in;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;

public interface JoinCampaignUseCase {
  CharacterResponse joinCampaign(Long characterId, Long campaignId);
}
