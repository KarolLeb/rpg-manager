package com.rpgmanager.backend.campaign;

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
class CampaignServiceTest {

    @Mock
    private CampaignRepository campaignRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CampaignService campaignService;

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
        Campaign savedCampaign = Campaign.builder()
                .id(10L)
                .name("New Camp")
                .description("Desc")
                .gameMaster(user)
                .status(Campaign.CampaignStatus.ACTIVE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(campaignRepository.save(any(Campaign.class))).thenReturn(savedCampaign);

        CampaignDTO result = campaignService.createCampaign(request);

        assertThat(result.getName()).isEqualTo("New Camp");
        assertThat(result.getGameMasterId()).isEqualTo(1L);
        verify(campaignRepository).save(any(Campaign.class));
    }
}
