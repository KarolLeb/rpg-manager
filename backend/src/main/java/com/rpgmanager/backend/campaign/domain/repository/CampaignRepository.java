package com.rpgmanager.backend.campaign.domain.repository;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
  List<CampaignDomain> findAll();

  Optional<CampaignDomain> findById(Long id);

  CampaignDomain save(CampaignDomain campaign);

  void deleteById(Long id);

  boolean existsById(Long id);
}
