package com.rpgmanager.backend.campaign.domain.repository;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import java.util.List;
import java.util.Optional;

/** Repository port for Campaign domain. */
public interface CampaignRepository {
  /**
   * Retrieves all campaigns.
   *
   * @return a list of all campaigns
   */
  List<CampaignDomain> findAll();

  /**
   * Finds a campaign by ID.
   *
   * @param id the campaign ID
   * @return an optional containing the campaign if found
   */
  Optional<CampaignDomain> findById(Long id);

  /**
   * Saves a campaign.
   *
   * @param campaign the campaign to save
   * @return the saved campaign
   */
  CampaignDomain save(CampaignDomain campaign);

  /**
   * Deletes a campaign by ID.
   *
   * @param id the campaign ID
   */
  void deleteById(Long id);

  /**
   * Checks if a campaign exists by ID.
   *
   * @param id the campaign ID
   * @return true if the campaign exists, false otherwise
   */
  boolean existsById(Long id);
}
