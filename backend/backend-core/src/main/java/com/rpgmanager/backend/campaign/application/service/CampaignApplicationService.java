package com.rpgmanager.backend.campaign.application.service;

import com.rpgmanager.backend.activitylog.ActivityEvent;
import com.rpgmanager.backend.activitylog.ActivityLogEntry;
import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;
import com.rpgmanager.backend.campaign.application.mapper.CampaignApplicationMapper;
import com.rpgmanager.backend.campaign.application.port.in.CreateCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.DeleteCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.GetCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.UpdateCampaignUseCase;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.domain.repository.CampaignRepository;
import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import com.rpgmanager.common.security.UserContext;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service implementation for Campaign application use cases. */
@Service
@RequiredArgsConstructor
public class CampaignApplicationService
    implements CreateCampaignUseCase,
        GetCampaignUseCase,
        UpdateCampaignUseCase,
        DeleteCampaignUseCase {

  private static final String CAMPAIGN_NOT_FOUND_MSG = "Campaign not found with id: ";
  private static final String USER_NOT_FOUND_MSG = "User not found with id: ";
  private final CampaignRepository campaignRepository;
  private final UserRepositoryPort userRepository;
  private final CampaignApplicationMapper campaignApplicationMapper;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Retrieves all campaigns.
   *
   * @return a list of all campaign DTOs
   */
  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "campaigns")
  public List<CampaignDto> getAllCampaigns() {
    return campaignRepository.findAll().stream().map(campaignApplicationMapper::toDto).toList();
  }

  /**
   * Retrieves a specific campaign by ID.
   *
   * @param id the campaign ID
   * @return the campaign DTO
   */
  @Override
  @Transactional(readOnly = true)
  public CampaignDto getCampaignById(Long id) {
    CampaignDomain campaign =
        campaignRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException(CAMPAIGN_NOT_FOUND_MSG + id));
    return campaignApplicationMapper.toDto(campaign);
  }

  /**
   * Creates a new campaign.
   *
   * @param request the campaign creation request
   * @return the created campaign DTO
   */
  @Override
  @Transactional
  @CacheEvict(value = "campaigns", allEntries = true)
  public CampaignDto createCampaign(CreateCampaignRequest request) {
    UserDomain gameMaster =
        userRepository
            .findById(request.getGameMasterId())
            .orElseThrow(
                () -> new IllegalArgumentException(USER_NOT_FOUND_MSG + request.getGameMasterId()));

    CampaignDomain campaign =
        CampaignDomain.builder()
            .name(request.getName())
            .description(request.getDescription())
            .status(CampaignDomain.CampaignStatus.ACTIVE)
            .creationDate(OffsetDateTime.now())
            .gameMasterId(gameMaster.getId())
            .gameMasterName(gameMaster.getUsername())
            .build();

    CampaignDomain savedCampaign = campaignRepository.save(campaign);
    eventPublisher.publishEvent(
        new ActivityEvent(
            ActivityLogEntry.ActionType.NOTE,
            String.format("Campaign '%s' created", savedCampaign.getName()),
            null,
            savedCampaign.getId(),
            gameMaster.getId(),
            Map.of("campaignName", savedCampaign.getName())));
    return campaignApplicationMapper.toDto(savedCampaign);
  }

  /**
   * Updates an existing campaign.
   *
   * @param id the ID of the campaign to update
   * @param request the campaign update request
   * @return the updated campaign DTO
   */
  @Override
  @Transactional
  @CacheEvict(value = "campaigns", allEntries = true)
  public CampaignDto updateCampaign(Long id, CreateCampaignRequest request) {
    CampaignDomain campaign =
        campaignRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException(CAMPAIGN_NOT_FOUND_MSG + id));

    checkCampaignOwnership(campaign);

    campaign.setName(request.getName());
    campaign.setDescription(request.getDescription());

    if (request.getGameMasterId() != null
        && !request.getGameMasterId().equals(campaign.getGameMasterId())) {
      UserDomain newGameMaster =
          userRepository
              .findById(request.getGameMasterId())
              .orElseThrow(
                  () ->
                      new IllegalArgumentException(USER_NOT_FOUND_MSG + request.getGameMasterId()));
      campaign.setGameMasterId(newGameMaster.getId());
      campaign.setGameMasterName(newGameMaster.getUsername());
    }

    CampaignDto result = campaignApplicationMapper.toDto(campaignRepository.save(campaign));
    eventPublisher.publishEvent(
        new ActivityEvent(
            ActivityLogEntry.ActionType.NOTE,
            String.format("Campaign '%s' updated", campaign.getName()),
            null,
            id,
            null,
            Map.of("campaignName", campaign.getName())));
    return result;
  }

  /**
   * Deletes a campaign by ID.
   *
   * @param id the ID of the campaign to delete
   */
  @Override
  @Transactional
  @CacheEvict(value = "campaigns", allEntries = true)
  public void deleteCampaign(Long id) {
    CampaignDomain campaign =
        campaignRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException(CAMPAIGN_NOT_FOUND_MSG + id));

    checkCampaignOwnership(campaign);
    campaignRepository.deleteById(id);
    eventPublisher.publishEvent(
        new ActivityEvent(
            ActivityLogEntry.ActionType.NOTE,
            String.format("Campaign %d deleted", id),
            null,
            id,
            null,
            Map.of("campaignId", id)));
  }

  private void checkCampaignOwnership(CampaignDomain campaign) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principal instanceof UserContext userContext) {
      boolean isAdmin =
          userContext.getAuthorities().stream()
              .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
      if (!isAdmin && !userContext.getUserId().equals(campaign.getGameMasterId())) {
        throw new AccessDeniedException(
            "Only the Game Master or an Admin can modify this campaign.");
      }
    } else {
      throw new AccessDeniedException("User not authenticated.");
    }
  }
}
