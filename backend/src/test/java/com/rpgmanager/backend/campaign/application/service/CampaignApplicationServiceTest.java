package com.rpgmanager.backend.campaign.application.service;

import com.rpgmanager.backend.campaign.application.dto.CampaignDTO;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.domain.repository.CampaignRepository;
import com.rpgmanager.backend.user.User;
import com.rpgmanager.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignApplicationServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CampaignApplicationService campaignService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("gm")
                .role(User.Role.GM)
                .build();
    }

    @Test
    void createCampaign_shouldSaveAndReturnDTO() {
        CreateCampaignRequest request = new CreateCampaignRequest("New Camp", "Desc", 1L);
        CampaignDomain savedCampaign = CampaignDomain.builder()
                .id(10L)
                .name("New Camp")
                .description("Desc")
                .gameMasterId(1L)
                .gameMasterName("gm")
                .status(CampaignDomain.CampaignStatus.ACTIVE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(campaignRepository.save(any(CampaignDomain.class))).thenReturn(savedCampaign);

        CampaignDTO result = campaignService.createCampaign(request);

        assertThat(result.getName()).isEqualTo("New Camp");
        assertThat(result.getGameMasterId()).isEqualTo(1L);
        verify(campaignRepository).save(any(CampaignDomain.class));
    }

    @Test
    void createCampaign_shouldThrowExceptionWhenUserNotFound() {
        CreateCampaignRequest request = new CreateCampaignRequest("New Camp", "Desc", 999L);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> campaignService.createCampaign(request));
    }

    @Test
    void getAllCampaigns_shouldReturnList() {
        CampaignDomain campaign = CampaignDomain.builder().id(1L).gameMasterId(1L).gameMasterName("gm").build();
        when(campaignRepository.findAll()).thenReturn(java.util.List.of(campaign));

        java.util.List<CampaignDTO> result = campaignService.getAllCampaigns();

        assertThat(result).hasSize(1);
    }

    @Test
    void getCampaignById_shouldReturnDTO() {
        CampaignDomain campaign = CampaignDomain.builder().id(1L).gameMasterId(1L).gameMasterName("gm").build();
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));

        CampaignDTO result = campaignService.getCampaignById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getCampaignById_shouldThrowExceptionWhenNotFound() {
        when(campaignRepository.findById(1L)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> campaignService.getCampaignById(1L));
    }

    @Test
    void updateCampaign_shouldUpdateAndReturnDTO() {
        CampaignDomain campaign = CampaignDomain.builder().id(1L).name("Old").gameMasterId(1L).gameMasterName("gm").build();
        CreateCampaignRequest request = new CreateCampaignRequest("New", "Desc", 1L);
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(CampaignDomain.class))).thenReturn(campaign);

        CampaignDTO result = campaignService.updateCampaign(1L, request);

        assertThat(result.getName()).isEqualTo("New");
    }

    @Test
    void updateCampaign_shouldNotUpdateGM_whenGMIdIsNull() {
        CampaignDomain campaign = CampaignDomain.builder().id(1L).name("Old").gameMasterId(1L).gameMasterName("gm").build();
        CreateCampaignRequest request = new CreateCampaignRequest("New", "Desc", null);
        when(campaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any(CampaignDomain.class))).thenReturn(campaign);

        campaignService.updateCampaign(1L, request);

        verify(userRepository, never()).findById(anyLong());
        assertThat(campaign.getGameMasterId()).isEqualTo(1L);
    }

    @Test
    void deleteCampaign_shouldDelete() {
        when(campaignRepository.existsById(1L)).thenReturn(true);

        campaignService.deleteCampaign(1L);

        verify(campaignRepository).deleteById(1L);
    }

    @Test
    void deleteCampaign_shouldThrowExceptionWhenNotFound() {
        when(campaignRepository.existsById(1L)).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> campaignService.deleteCampaign(1L));
    }
}