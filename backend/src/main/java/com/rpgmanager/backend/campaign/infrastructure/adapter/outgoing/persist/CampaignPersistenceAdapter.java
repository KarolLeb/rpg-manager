package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.domain.repository.CampaignRepository;
import com.rpgmanager.backend.campaign.infrastructure.mapper.CampaignPersistenceMapper;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adapter implementation for Campaign persistence. */
@Component
@RequiredArgsConstructor
public class CampaignPersistenceAdapter implements CampaignRepository {

  private final JpaCampaignRepository jpaCampaignRepository;
  private final UserRepositoryPort userRepository;

  /**
   * Retrieves all campaigns.
   *
   * @return a list of all campaigns
   */
  @Override
  public List<CampaignDomain> findAll() {
    return jpaCampaignRepository.findAll().stream()
        .map(CampaignPersistenceMapper::toDomain)
        .map(this::enrichWithGameMasterName)
        .toList();
  }

  /**
   * Finds a campaign by its ID.
   *
   * @param id the campaign ID
   * @return an optional containing the campaign if found
   */
  @Override
  public Optional<CampaignDomain> findById(Long id) {
    return jpaCampaignRepository
        .findById(id)
        .map(CampaignPersistenceMapper::toDomain)
        .map(this::enrichWithGameMasterName);
  }

  /**
   * Saves a campaign.
   *
   * @param campaign the campaign domain object to save
   * @return the saved campaign domain object
   */
  @Override
  public CampaignDomain save(CampaignDomain campaign) {
    CampaignEntity entity = CampaignPersistenceMapper.toEntity(campaign);
    CampaignEntity savedEntity = jpaCampaignRepository.save(entity);
    return enrichWithGameMasterName(CampaignPersistenceMapper.toDomain(savedEntity));
  }

  private CampaignDomain enrichWithGameMasterName(CampaignDomain campaign) {
    if (campaign != null && campaign.getGameMasterId() != null) {
      userRepository
          .findById(campaign.getGameMasterId())
          .ifPresent(user -> campaign.setGameMasterName(user.getUsername()));
    }
    return campaign;
  }

  /**
   * Deletes a campaign by its ID.
   *
   * @param id the campaign ID
   */
  @Override
  public void deleteById(Long id) {
    jpaCampaignRepository.deleteById(id);
  }

  /**
   * Checks if a campaign exists by its ID.
   *
   * @param id the campaign ID
   * @return true if the campaign exists, false otherwise
   */
  @Override
  public boolean existsById(Long id) {
    return jpaCampaignRepository.existsById(id);
  }
}
