package com.rpgmanager.backend.session;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
  List<Session> findByCampaignId(Long campaignId);
}
