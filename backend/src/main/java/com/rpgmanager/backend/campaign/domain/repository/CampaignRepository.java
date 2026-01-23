package com.rpgmanager.backend.campaign.domain.repository;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignRepository {
  List<CampaignDomain> findAll();

  Optional<CampaignDomain> findById(Long id);

  Optional<CampaignDomain> findByUuid(UUID uuid);

  CampaignDomain save(CampaignDomain campaign);

  void deleteById(Long id);

  boolean existsById(Long id);
}
