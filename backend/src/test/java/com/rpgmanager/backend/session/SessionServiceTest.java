package com.rpgmanager.backend.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

  @Mock private SessionRepository sessionRepository;

  @Mock private JpaCampaignRepository campaignRepository;

  @InjectMocks private SessionService sessionService;

  private CampaignEntity campaign;
  private Session session;

  @BeforeEach
  void setUp() {
    campaign = CampaignEntity.builder().id(1L).name("Campaign 1").build();

    session =
        Session.builder()
            .id(100L)
            .campaign(campaign)
            .name("Session 1")
            .sessionDate(OffsetDateTime.now())
            .status(Session.SessionStatus.ACTIVE)
            .build();
  }

  @Test
  void createSession_shouldSaveAndReturnDTO() {
    CreateSessionRequest request =
        new CreateSessionRequest(1L, "New Session", "Desc", OffsetDateTime.now());

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(sessionRepository.save(any(Session.class)))
        .thenAnswer(
            invocation -> {
              Session s = invocation.getArgument(0);
              s.setId(101L);
              return s;
            });

    SessionDto result = sessionService.createSession(request);

    assertThat(result.getName()).isEqualTo("New Session");
    assertThat(result.getCampaignId()).isEqualTo(1L);
    verify(sessionRepository).save(any(Session.class));
  }

  @Test
  void getSession_shouldReturnDTO() {
    when(sessionRepository.findById(100L)).thenReturn(Optional.of(session));

    SessionDto result = sessionService.getSession(100L);

    assertThat(result.getId()).isEqualTo(100L);
  }

  @Test
  void cancelSession_shouldSetStatusCancelled() {
    when(sessionRepository.findById(100L)).thenReturn(Optional.of(session));

    sessionService.cancelSession(100L);

    assertThat(session.getStatus()).isEqualTo(Session.SessionStatus.CANCELLED);
    verify(sessionRepository).save(session);
  }

  @Test
  void completeSession_shouldSetStatusFinished() {
    when(sessionRepository.findById(100L)).thenReturn(Optional.of(session));

    sessionService.completeSession(100L);

    assertThat(session.getStatus()).isEqualTo(Session.SessionStatus.FINISHED);
    verify(sessionRepository).save(session);
  }
}
