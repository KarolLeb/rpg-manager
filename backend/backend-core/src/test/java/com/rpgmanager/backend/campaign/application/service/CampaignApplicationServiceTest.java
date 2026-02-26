package com.rpgmanager.backend.campaign.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;
import com.rpgmanager.backend.campaign.application.mapper.CampaignApplicationMapper;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.domain.repository.CampaignRepository;
import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class CampaignApplicationServiceTest {

  @Mock private CampaignRepository campaignRepository;

  @Mock private UserRepositoryPort userRepository;

  @Mock private CampaignApplicationMapper campaignApplicationMapper;
  @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;

  @InjectMocks private CampaignApplicationService campaignService;

  private UserDomain user;

  @BeforeEach
  void setUp() {
    user = Instancio.create(UserDomain.class);
    user.setId(1L);
    mockSecurityContext(1L, "GM");
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private void mockSecurityContext(Long userId, String role) {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    com.rpgmanager.common.security.UserContext userContext =
        new com.rpgmanager.common.security.UserContext(
            "user",
            "pass",
            java.util.List.of(
                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                    "ROLE_" + role)),
            userId);

    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    lenient().when(authentication.getPrincipal()).thenReturn(userContext);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void createCampaign_shouldSaveAndReturnDTO() {
    CreateCampaignRequest request = Instancio.create(CreateCampaignRequest.class);
    request.setGameMasterId(1L);

    CampaignDomain savedCampaign = Instancio.create(CampaignDomain.class);
    CampaignDto campaignDto = Instancio.create(CampaignDto.class);
    campaignDto.setName(request.getName());
    campaignDto.setGameMasterId(1L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(campaignRepository.save(any(CampaignDomain.class))).thenReturn(savedCampaign);
    when(campaignApplicationMapper.toDto(savedCampaign)).thenReturn(campaignDto);

    CampaignDto result = campaignService.createCampaign(request);

    assertThat(result.getName()).isEqualTo(request.getName());
    assertThat(result.getGameMasterId()).isEqualTo(1L);
    verify(eventPublisher)
        .publishEvent(any(com.rpgmanager.backend.activitylog.ActivityEvent.class));
    verify(campaignRepository)
        .save(
            argThat(
                campaign -> {
                  assertThat(campaign.getName()).isEqualTo(request.getName());
                  assertThat(campaign.getDescription()).isEqualTo(request.getDescription());
                  assertThat(campaign.getGameMasterId()).isEqualTo(1L);
                  return true;
                }));
  }

  @Test
  void createCampaign_shouldThrowExceptionWhenUserNotFound() {
    CreateCampaignRequest request = Instancio.create(CreateCampaignRequest.class);
    request.setGameMasterId(999L);

    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> campaignService.createCampaign(request));
  }

  @Test
  void getAllCampaigns_shouldReturnList() {
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    CampaignDto campaignDto = Instancio.create(CampaignDto.class);

    when(campaignRepository.findAll()).thenReturn(java.util.List.of(campaign));
    when(campaignApplicationMapper.toDto(campaign)).thenReturn(campaignDto);

    java.util.List<CampaignDto> result = campaignService.getAllCampaigns();

    assertThat(result).hasSize(1);
  }

  @Test
  void getCampaignById_shouldReturnDTO() {
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    CampaignDto campaignDto = Instancio.create(CampaignDto.class);
    campaignDto.setId(1L);

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(campaignApplicationMapper.toDto(campaign)).thenReturn(campaignDto);

    CampaignDto result = campaignService.getCampaignById(1L);

    assertThat(result.getId()).isEqualTo(1L);
  }

  @Test
  void getCampaignById_shouldThrowExceptionWhenNotFound() {
    when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> campaignService.getCampaignById(1L));
  }

  @Test
  void updateCampaign_shouldUpdateAndReturnDTO() {
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L);

    CreateCampaignRequest request = Instancio.create(CreateCampaignRequest.class);
    request.setGameMasterId(1L);

    CampaignDto campaignDto = Instancio.create(CampaignDto.class);
    campaignDto.setName(request.getName());

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(campaignRepository.save(any(CampaignDomain.class))).thenReturn(campaign);
    when(campaignApplicationMapper.toDto(campaign)).thenReturn(campaignDto);

    CampaignDto result = campaignService.updateCampaign(1L, request);

    assertThat(result.getName()).isEqualTo(request.getName());
    verify(eventPublisher)
        .publishEvent(any(com.rpgmanager.backend.activitylog.ActivityEvent.class));
    verify(campaignRepository)
        .save(
            argThat(
                c -> {
                  assertThat(c.getName()).isEqualTo(request.getName());
                  assertThat(c.getDescription()).isEqualTo(request.getDescription());
                  return true;
                }));
  }

  @Test
  void updateCampaign_shouldUpdateGameMaster_whenChanged() {
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L);

    CreateCampaignRequest request = Instancio.create(CreateCampaignRequest.class);
    request.setGameMasterId(2L);

    UserDomain newGM = Instancio.create(UserDomain.class);
    newGM.setId(2L);
    newGM.setUsername("newGM");

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(userRepository.findById(2L)).thenReturn(Optional.of(newGM));
    when(campaignRepository.save(any(CampaignDomain.class))).thenAnswer(i -> i.getArguments()[0]);
    when(campaignApplicationMapper.toDto(any())).thenReturn(Instancio.create(CampaignDto.class));

    campaignService.updateCampaign(1L, request);

    assertThat(campaign.getGameMasterId()).isEqualTo(2L);
    assertThat(campaign.getGameMasterName()).isEqualTo("newGM");
  }

  @Test
  void updateCampaign_shouldThrowException_whenCampaignNotFound() {
    CreateCampaignRequest request = Instancio.create(CreateCampaignRequest.class);
    when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> campaignService.updateCampaign(1L, request));
  }

  @Test
  void updateCampaign_shouldThrowException_whenUserNotFound() {
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L);
    CreateCampaignRequest request = Instancio.create(CreateCampaignRequest.class);
    request.setGameMasterId(2L);

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(userRepository.findById(2L)).thenReturn(Optional.empty());

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> campaignService.updateCampaign(1L, request));
  }

  @Test
  void updateCampaign_shouldNotUpdateGM_whenGMIdIsNull() {
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L);

    CreateCampaignRequest request = Instancio.create(CreateCampaignRequest.class);
    request.setGameMasterId(null);

    CampaignDto campaignDto = Instancio.create(CampaignDto.class);

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(campaignRepository.save(any(CampaignDomain.class))).thenReturn(campaign);
    when(campaignApplicationMapper.toDto(campaign)).thenReturn(campaignDto);

    campaignService.updateCampaign(1L, request);

    verify(userRepository, never()).findById(anyLong());
    assertThat(campaign.getGameMasterId()).isEqualTo(1L);
  }

  @Test
  void updateCampaign_shouldNotUpdateGameMaster_whenIdIsSame() {
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L);

    CreateCampaignRequest request = Instancio.create(CreateCampaignRequest.class);
    request.setGameMasterId(1L); // Same ID

    CampaignDto campaignDto = Instancio.create(CampaignDto.class);

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(campaignRepository.save(any(CampaignDomain.class))).thenReturn(campaign);
    when(campaignApplicationMapper.toDto(campaign)).thenReturn(campaignDto);

    campaignService.updateCampaign(1L, request);

    verify(userRepository, never()).findById(anyLong());
    assertThat(campaign.getGameMasterId()).isEqualTo(1L);
  }

  @Test
  void updateCampaign_shouldThrowAccessDenied_whenUserIsNotGMOrAdmin() {
    mockSecurityContext(2L, "USER"); // Not GM (1), not Admin
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L);

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

    CreateCampaignRequest request = new CreateCampaignRequest();
    request.setName("New Name");

    org.junit.jupiter.api.Assertions.assertThrows(
        org.springframework.security.access.AccessDeniedException.class,
        () -> campaignService.updateCampaign(1L, request));
  }

  @Test
  void deleteCampaign_shouldDelete() {
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L); // Matches mocked security context

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

    campaignService.deleteCampaign(1L);

    verify(eventPublisher)
        .publishEvent(any(com.rpgmanager.backend.activitylog.ActivityEvent.class));
    verify(campaignRepository).deleteById(1L);
  }

  @Test
  void deleteCampaign_shouldAllowAdminToDelete() {
    mockSecurityContext(99L, "ADMIN");
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L); // GM is 1, but user is 99 (Admin)

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

    campaignService.deleteCampaign(1L);

    verify(campaignRepository).deleteById(1L);
  }

  @Test
  void deleteCampaign_shouldThrowAccessDenied_whenUserIsNotGMOrAdmin() {
    mockSecurityContext(2L, "USER"); // Not GM (1), not Admin
    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);
    campaign.setGameMasterId(1L);

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

    org.junit.jupiter.api.Assertions.assertThrows(
        org.springframework.security.access.AccessDeniedException.class,
        () -> campaignService.deleteCampaign(1L));
  }

  @Test
  void deleteCampaign_shouldThrowAccessDenied_whenPrincipalIsNotUserContext() {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn("not-user-context");
    SecurityContextHolder.setContext(securityContext);

    CampaignDomain campaign = Instancio.create(CampaignDomain.class);
    campaign.setId(1L);

    when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

    org.junit.jupiter.api.Assertions.assertThrows(
        org.springframework.security.access.AccessDeniedException.class,
        () -> campaignService.deleteCampaign(1L));
  }

  @Test
  void deleteCampaign_shouldThrowExceptionWhenNotFound() {
    when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class, () -> campaignService.deleteCampaign(1L));
  }
}
