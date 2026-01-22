package com.rpgmanager.backend;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import com.rpgmanager.backend.config.TestContainersConfig;
import com.rpgmanager.backend.session.Session;
import com.rpgmanager.backend.session.SessionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestContainersConfig.class)
class RpgmanagerBackendApplicationTests {

	@Autowired
	private JpaCampaignRepository campaignRepository;

	@Autowired
	private SessionRepository sessionRepository;

	@Test
	void contextLoadsAndMigrationsAreApplied() {
		// 1. Sprawdź czy kampania z V2 istnieje
		Optional<CampaignEntity> campaignOpt = campaignRepository.findByName("Kampania Smoczej Lancy");
		assertThat(campaignOpt).isPresent();

		CampaignEntity campaign = campaignOpt.get();
		assertThat(campaign.getUuid()).isNotNull();
		assertThat(campaign.getStatus().name()).isEqualTo("ACTIVE");

		// 2. Sprawdź czy sesje są przypisane do kampanii
		List<Session> sessions = sessionRepository.findByCampaignId(campaign.getId());
		assertThat(sessions).hasSizeGreaterThanOrEqualTo(2); // V2 dodaje 2 sesje

		// 3. Sprawdź szczegóły sesji
		boolean hasInnMeeting = sessions.stream()
				.anyMatch(s -> s.getName().equals("Sesja 1: Spotkanie w Karczmie"));
		assertThat(hasInnMeeting).isTrue();
	}

}