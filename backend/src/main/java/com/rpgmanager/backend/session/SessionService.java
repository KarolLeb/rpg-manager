package com.rpgmanager.backend.session;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SessionService {

  private static final String SESSION_NOT_FOUND_MSG = "Session not found with id: ";
  private final SessionRepository sessionRepository;
  private final JpaCampaignRepository campaignRepository;

  @Transactional
  public SessionDTO createSession(CreateSessionRequest request) {
    CampaignEntity campaign =
        campaignRepository
            .findById(request.getCampaignId())
            .orElseThrow(
                () ->
                    new RuntimeException("Campaign not found with id: " + request.getCampaignId()));

    Session session =
        Session.builder()
            .campaign(campaign)
            .name(request.getName())
            .description(request.getDescription())
            .sessionDate(request.getSessionDate())
            .status(Session.SessionStatus.ACTIVE)
            .build();

    Session savedSession = sessionRepository.save(session);
    return toDTO(savedSession);
  }

  @Transactional(readOnly = true)
  public SessionDTO getSession(Long id) {
    Session session =
        sessionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND_MSG + id));
    return toDTO(session);
  }

  @Transactional(readOnly = true)
  public List<SessionDTO> getSessionsByCampaign(Long campaignId) {
    return sessionRepository.findByCampaignId(campaignId).stream().map(this::toDTO).toList();
  }

  @Transactional
  public SessionDTO updateSession(Long id, CreateSessionRequest request) {
    Session session =
        sessionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND_MSG + id));

    session.setName(request.getName());
    session.setDescription(request.getDescription());
    session.setSessionDate(request.getSessionDate());

    return toDTO(sessionRepository.save(session));
  }

  @Transactional
  public void cancelSession(Long id) {
    Session session =
        sessionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND_MSG + id));
    session.setStatus(Session.SessionStatus.CANCELLED);
    sessionRepository.save(session);
  }

  @Transactional
  public void completeSession(Long id) {
    Session session =
        sessionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND_MSG + id));
    session.setStatus(Session.SessionStatus.FINISHED);
    sessionRepository.save(session);
  }

  private SessionDTO toDTO(Session session) {
    return SessionDTO.builder()
        .id(session.getId())
        .campaignId(session.getCampaign().getId())
        .campaignName(session.getCampaign().getName())
        .name(session.getName())
        .description(session.getDescription())
        .sessionDate(session.getSessionDate())
        .status(session.getStatus())
        .build();
  }
}
