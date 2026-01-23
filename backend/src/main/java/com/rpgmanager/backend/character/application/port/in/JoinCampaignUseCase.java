package com.rpgmanager.backend.character.application.port.in;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import java.util.UUID;

public interface JoinCampaignUseCase {
  CharacterResponse joinCampaign(UUID characterUuid, Long campaignId);
}
