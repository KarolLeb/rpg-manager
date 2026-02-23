package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.in.web;

import com.rpgmanager.backend.actionpolicy.application.service.ActionPermissionService;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class ActionPermissionController {

  private final ActionPermissionService actionPermissionService;

  @GetMapping("/check")
  public ResponseEntity<PermissionCheckResponse> checkPermission(
      @RequestParam Long characterId,
      @RequestParam ActionType actionType,
      @RequestParam(required = false) Long campaignId,
      @RequestParam(required = false) Long sessionId) {

    boolean isAllowed =
        actionPermissionService.canPerformAction(characterId, actionType, campaignId, sessionId);

    return ResponseEntity.ok(new PermissionCheckResponse(isAllowed));
  }

  public record PermissionCheckResponse(boolean allowed) {}
}
