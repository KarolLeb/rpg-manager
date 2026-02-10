package com.rpgmanager.backend.character.application.port.in;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;

/** Use case for a character joining a campaign. */
public interface JoinCampaignUseCase {

  /**
   * Allows a character to join a campaign.
   *
   * @param characterId the ID of the character
   * @param campaignId the ID of the campaign
   * @return the updated character response
   */
  CharacterResponse joinCampaign(Long characterId, Long campaignId);
}
