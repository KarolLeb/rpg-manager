package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCampaignRepository extends JpaRepository<CampaignEntity, Long> {}
