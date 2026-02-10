package com.rpgmanager.backend.session;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for managing game sessions. */
@Service
@RequiredArgsConstructor
public class SessionService {

  private static final String SESSION_NOT_FOUND_MSG = "Session not found with id: ";
  private final SessionRepository sessionRepository;
  private final JpaCampaignRepository campaignRepository;

  /**
   * Creates a new session for a campaign.
   *
   * @param request the session creation request
   * @return the created session DTO
   */
  @Transactional
  public SessionDto createSession(CreateSessionRequest request) {
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
    return toDto(savedSession);
  }

  /**
   * Retrieves a session by its ID.
   *
   * @param id the ID of the session
   * @return the session DTO
   */
  @Transactional(readOnly = true)
  public SessionDto getSession(Long id) {
    Session session =
        sessionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND_MSG + id));
    return toDto(session);
  }

  /**
   * Retrieves all sessions associated with a specific campaign.
   *
   * @param campaignId the ID of the campaign
   * @return a list of session DTOs
   */
  @Transactional(readOnly = true)
  public List<SessionDto> getSessionsByCampaign(Long campaignId) {
    return sessionRepository.findByCampaignId(campaignId).stream().map(this::toDto).toList();
  }

  /**
   * Updates an existing session.
   *
   * @param id the ID of the session to update
   * @param request the updated session details
   * @return the updated session DTO
   */
  @Transactional
  public SessionDto updateSession(Long id, CreateSessionRequest request) {
    Session session =
        sessionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND_MSG + id));

    session.setName(request.getName());
    session.setDescription(request.getDescription());
    session.setSessionDate(request.getSessionDate());

    return toDto(sessionRepository.save(session));
  }

  /**
   * Cancels a session.
   *
   * @param id the ID of the session to cancel
   */
  @Transactional
  public void cancelSession(Long id) {
    Session session =
        sessionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND_MSG + id));
    session.setStatus(Session.SessionStatus.CANCELLED);
    sessionRepository.save(session);
  }

  /**
   * Marks a session as completed.
   *
   * @param id the ID of the session to complete
   */
  @Transactional
  public void completeSession(Long id) {
    Session session =
        sessionRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(SESSION_NOT_FOUND_MSG + id));
    session.setStatus(Session.SessionStatus.FINISHED);
    sessionRepository.save(session);
  }

  private SessionDto toDto(Session session) {
    return SessionDto.builder()
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
