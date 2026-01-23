package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCampaignRepository extends JpaRepository<CampaignEntity, Long> {
  Optional<CampaignEntity> findByName(String name);

  Optional<CampaignEntity> findByUuid(UUID uuid);
}
