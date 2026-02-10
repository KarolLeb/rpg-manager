package com.rpgmanager.backend.campaign.application.port.in;

/** Use case for deleting campaigns. */
public interface DeleteCampaignUseCase {

  /**
   * Deletes a campaign by its ID.
   *
   * @param id the ID of the campaign to delete
   */
  void deleteCampaign(Long id);
}
