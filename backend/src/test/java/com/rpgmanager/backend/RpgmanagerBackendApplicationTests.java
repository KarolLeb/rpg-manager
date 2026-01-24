package com.rpgmanager.backend;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import com.rpgmanager.backend.session.Session;
import com.rpgmanager.backend.session.SessionRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RpgmanagerBackendApplicationTests extends BaseIntegrationTest {

  @Autowired private JpaCampaignRepository campaignRepository;

  @Autowired private SessionRepository sessionRepository;

  @Test
  void contextLoadsAndMigrationsAreApplied() {
    // 1. Sprawdź czy kampania z V2 istnieje
    List<CampaignEntity> campaigns = campaignRepository.findAll();
    assertThat(campaigns).isNotEmpty();

    CampaignEntity campaign = campaigns.get(0);
    assertThat(campaign.getStatus().name()).isEqualTo("ACTIVE");

    // 2. Sprawdź czy sesje są przypisane do kampanii
    List<Session> sessions = sessionRepository.findByCampaignId(campaign.getId());
    assertThat(sessions).hasSizeGreaterThanOrEqualTo(2); // V2 dodaje 2 sesje

    // 3. Sprawdź szczegóły sesji
    boolean hasInnMeeting =
        sessions.stream().anyMatch(s -> s.getName().equals("Sesja 1: Spotkanie w Karczmie"));
    assertThat(hasInnMeeting).isTrue();
  }
}
