package com.rpgmanager.backend.campaign.infrastructure.adapter.in.web;

import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;
import com.rpgmanager.backend.campaign.application.port.in.CreateCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.DeleteCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.GetCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.UpdateCampaignUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing campaign operations. */
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

  private final CreateCampaignUseCase createCampaignUseCase;
  private final GetCampaignUseCase getCampaignUseCase;
  private final UpdateCampaignUseCase updateCampaignUseCase;
  private final DeleteCampaignUseCase deleteCampaignUseCase;

  /**
   * Retrieves all campaigns.
   *
   * @return a response entity containing a list of all campaigns
   */
  @GetMapping
  public ResponseEntity<List<CampaignDto>> getAllCampaigns() {
    return ResponseEntity.ok(getCampaignUseCase.getAllCampaigns());
  }

  /**
   * Retrieves a specific campaign by ID.
   *
   * @param id the ID of the campaign to retrieve
   * @return a response entity containing the campaign details
   */
  @GetMapping("/{id}")
  public ResponseEntity<CampaignDto> getCampaignById(@PathVariable Long id) {
    return ResponseEntity.ok(getCampaignUseCase.getCampaignById(id));
  }

  /**
   * Creates a new campaign.
   *
   * @param request the campaign creation request
   * @return a response entity containing the created campaign
   */
  @PostMapping
  public ResponseEntity<CampaignDto> createCampaign(@RequestBody CreateCampaignRequest request) {
    return ResponseEntity.ok(createCampaignUseCase.createCampaign(request));
  }

  /**
   * Updates an existing campaign.
   *
   * @param id the ID of the campaign to update
   * @param request the campaign update request
   * @return a response entity containing the updated campaign
   */
  @PutMapping("/{id}")
  public ResponseEntity<CampaignDto> updateCampaign(
      @PathVariable Long id, @RequestBody CreateCampaignRequest request) {
    return ResponseEntity.ok(updateCampaignUseCase.updateCampaign(id, request));
  }

  /**
   * Deletes a campaign by ID.
   *
   * @param id the ID of the campaign to delete
   * @return a response entity with no content
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
    deleteCampaignUseCase.deleteCampaign(id);
    return ResponseEntity.noContent().build();
  }
}
