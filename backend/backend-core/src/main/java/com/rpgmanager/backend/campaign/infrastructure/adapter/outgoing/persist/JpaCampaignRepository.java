package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** JPA Repository for Campaign entities. */
@Repository
public interface JpaCampaignRepository extends JpaRepository<CampaignEntity, Long> {}
