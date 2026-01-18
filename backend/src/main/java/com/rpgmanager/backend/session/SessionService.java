package com.rpgmanager.backend.session;

import com.rpgmanager.backend.campaign.Campaign;
import com.rpgmanager.backend.campaign.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final CampaignRepository campaignRepository;

    @Transactional
    public SessionDTO createSession(CreateSessionRequest request) {
        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + request.getCampaignId()));

        Session session = Session.builder()
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
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        return toDTO(session);
    }

    @Transactional(readOnly = true)
    public List<SessionDTO> getSessionsByCampaign(Long campaignId) {
        return sessionRepository.findByCampaignId(campaignId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SessionDTO updateSession(Long id, CreateSessionRequest request) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));

        session.setName(request.getName());
        session.setDescription(request.getDescription());
        session.setSessionDate(request.getSessionDate());

        return toDTO(sessionRepository.save(session));
    }

    @Transactional
    public void cancelSession(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        session.setStatus(Session.SessionStatus.CANCELLED);
        sessionRepository.save(session);
    }

    @Transactional
    public void completeSession(Long id) {
        Session session = sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Session not found with id: " + id));
        session.setStatus(Session.SessionStatus.FINISHED);
        sessionRepository.save(session);
    }

    private SessionDTO toDTO(Session session) {
        return SessionDTO.builder()
                .id(session.getId())
                .uuid(session.getUuid())
                .campaignId(session.getCampaign().getId())
                .campaignName(session.getCampaign().getName())
                .name(session.getName())
                .description(session.getDescription())
                .sessionDate(session.getSessionDate())
                .status(session.getStatus())
                .build();
    }
}
