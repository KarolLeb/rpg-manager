package com.rpgmanager.backend.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByCampaignId(Long campaignId);
    Optional<Session> findByUuid(UUID uuid);
}
