package com.rpgmanager.backend.session;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public SessionDTO createSession(@RequestBody CreateSessionRequest request) {
        return sessionService.createSession(request);
    }

    @GetMapping("/{id}")
    public SessionDTO getSession(@PathVariable Long id) {
        return sessionService.getSession(id);
    }

    @GetMapping("/campaign/{campaignId}")
    public List<SessionDTO> getSessionsByCampaign(@PathVariable Long campaignId) {
        return sessionService.getSessionsByCampaign(campaignId);
    }

    @PutMapping("/{id}")
    public SessionDTO updateSession(@PathVariable Long id, @RequestBody CreateSessionRequest request) {
        return sessionService.updateSession(id, request);
    }

    @PostMapping("/{id}/cancel")
    public void cancelSession(@PathVariable Long id) {
        sessionService.cancelSession(id);
    }

    @PostMapping("/{id}/complete")
    public void completeSession(@PathVariable Long id) {
        sessionService.completeSession(id);
    }
}
