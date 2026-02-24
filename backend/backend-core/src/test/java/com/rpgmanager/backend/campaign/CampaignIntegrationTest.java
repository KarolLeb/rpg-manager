package com.rpgmanager.backend.campaign;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rpgmanager.backend.BaseIntegrationTest;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

class CampaignIntegrationTest extends BaseIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserRepositoryPort userRepository;

    @Autowired
    private JpaCampaignRepository campaignRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Long gameMasterId = 1L;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        UserDomain gameMaster = UserDomain.builder().id(gameMasterId).username("gamemaster")
                .roles(java.util.Set.of("GM")).build();
        given(userRepository.findById(gameMasterId)).willReturn(Optional.of(gameMaster));
    }

    @Test
    void shouldCreateCampaign() throws Exception {
        CreateCampaignRequest request = new CreateCampaignRequest("New Test Campaign", "A description", gameMasterId);

        mockMvc
                .perform(
                        post("/api/campaigns")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("New Test Campaign")))
                .andExpect(jsonPath("$.description", is("A description")))
                .andExpect(jsonPath("$.gameMasterId", is(gameMasterId.intValue())));
    }

    @Test
    void shouldGetAllCampaigns() throws Exception {
        // Create one campaign to ensure list is not empty
        CampaignEntity campaign = CampaignEntity.builder()
                .name("List Test Campaign")
                .description("Desc")
                .gameMasterId(gameMasterId)
                .status(CampaignEntity.CampaignStatus.ACTIVE)
                .build();
        campaignRepository.save(campaign);

        mockMvc
                .perform(get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].name", hasItem("List Test Campaign")));
    }

    @Test
    void shouldGetCampaignById() throws Exception {
        CampaignEntity campaign = CampaignEntity.builder()
                .name("Get By Id Campaign")
                .description("Desc")
                .gameMasterId(gameMasterId)
                .status(CampaignEntity.CampaignStatus.ACTIVE)
                .build();
        campaign = campaignRepository.save(campaign);

        mockMvc
                .perform(get("/api/campaigns/" + campaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(campaign.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Get By Id Campaign")));
    }

    @Test
    void shouldUpdateCampaign() throws Exception {
        CampaignEntity campaign = CampaignEntity.builder()
                .name("Original Name")
                .description("Original Desc")
                .gameMasterId(gameMasterId)
                .status(CampaignEntity.CampaignStatus.ACTIVE)
                .build();
        campaign = campaignRepository.save(campaign);

        CreateCampaignRequest updateRequest = new CreateCampaignRequest("Updated Name", "Updated Desc", gameMasterId);

        mockMvc
                .perform(
                        put("/api/campaigns/" + campaign.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.description", is("Updated Desc")));
    }

    @Test
    void shouldDeleteCampaign() throws Exception {
        CampaignEntity campaign = CampaignEntity.builder()
                .name("To Be Deleted")
                .description("Desc")
                .gameMasterId(gameMasterId)
                .status(CampaignEntity.CampaignStatus.ACTIVE)
                .build();
        campaign = campaignRepository.save(campaign);

        mockMvc.perform(delete("/api/campaigns/" + campaign.getId())).andExpect(status().isNoContent());

        assertThat(campaignRepository.existsById(campaign.getId())).isFalse();
    }
}
