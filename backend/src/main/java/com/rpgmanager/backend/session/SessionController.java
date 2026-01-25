package com.rpgmanager.backend.session;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing session operations. */
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

  private final SessionService sessionService;

  /**
   * Creates a new session.
   *
   * @param request the session creation request
   * @return the created session DTO
   */
  @PostMapping
  public SessionDto createSession(@RequestBody CreateSessionRequest request) {
    return sessionService.createSession(request);
  }

  /**
   * Retrieves a session by ID.
   *
   * @param id the ID of the session to retrieve
   * @return the session DTO
   */
  @GetMapping("/{id}")
  public SessionDto getSession(@PathVariable Long id) {
    return sessionService.getSession(id);
  }

  /**
   * Retrieves all sessions for a specific campaign.
   *
   * @param campaignId the ID of the campaign
   * @return a list of session DTOs
   */
  @GetMapping("/campaign/{campaignId}")
  public List<SessionDto> getSessionsByCampaign(@PathVariable Long campaignId) {
    return sessionService.getSessionsByCampaign(campaignId);
  }

  /**
   * Updates an existing session.
   *
   * @param id the ID of the session to update
   * @param request the session update request
   * @return the updated session DTO
   */
  @PutMapping("/{id}")
  public SessionDto updateSession(
      @PathVariable Long id, @RequestBody CreateSessionRequest request) {
    return sessionService.updateSession(id, request);
  }

  /**
   * Cancels a session.
   *
   * @param id the ID of the session to cancel
   */
  @PostMapping("/{id}/cancel")
  public void cancelSession(@PathVariable Long id) {
    sessionService.cancelSession(id);
  }

  /**
   * Completes a session.
   *
   * @param id the ID of the session to complete
   */
  @PostMapping("/{id}/complete")
  public void completeSession(@PathVariable Long id) {
    sessionService.completeSession(id);
  }
}
