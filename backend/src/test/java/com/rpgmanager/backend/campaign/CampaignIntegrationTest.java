package com.rpgmanager.backend.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;
import com.rpgmanager.backend.campaign.infrastructure.adapter.out.persistence.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.out.persistence.JpaCampaignRepository;
import com.rpgmanager.backend.config.TestContainersConfig;
import com.rpgmanager.backend.user.User;
import com.rpgmanager.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestContainersConfig.class)
class CampaignIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JpaCampaignRepository campaignRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User gameMaster;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();

        // Ensure we have a user to be the GM
        gameMaster = userRepository.findByUsername("gamemaster")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("gamemaster")
                        .password("password")
                        .email("gm@test.com")
                        .role(User.Role.GM)
                        .build()));
    }

    @Test
    void shouldCreateCampaign() throws Exception {
        CreateCampaignRequest request = new CreateCampaignRequest("New Test Campaign", "A description", gameMaster.getId());

        mockMvc.perform(post("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("New Test Campaign")))
                .andExpect(jsonPath("$.description", is("A description")))
                .andExpect(jsonPath("$.gameMasterId", is(gameMaster.getId().intValue())));
    }

    @Test
    void shouldGetAllCampaigns() throws Exception {
        // Create one campaign to ensure list is not empty
        CampaignEntity campaign = CampaignEntity.builder()
                .name("List Test Campaign")
                .description("Desc")
                .gameMaster(gameMaster)
                .status(CampaignEntity.CampaignStatus.ACTIVE)
                .build();
        campaignRepository.save(campaign);

        mockMvc.perform(get("/api/campaigns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].name", hasItem("List Test Campaign")));
    }

    @Test
    void shouldGetCampaignById() throws Exception {
        CampaignEntity campaign = CampaignEntity.builder()
                .name("Get By Id Campaign")
                .description("Desc")
                .gameMaster(gameMaster)
                .status(CampaignEntity.CampaignStatus.ACTIVE)
                .build();
        campaign = campaignRepository.save(campaign);

        mockMvc.perform(get("/api/campaigns/" + campaign.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(campaign.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Get By Id Campaign")));
    }

    @Test
    void shouldUpdateCampaign() throws Exception {
        CampaignEntity campaign = CampaignEntity.builder()
                .name("Original Name")
                .description("Original Desc")
                .gameMaster(gameMaster)
                .status(CampaignEntity.CampaignStatus.ACTIVE)
                .build();
        campaign = campaignRepository.save(campaign);

        CreateCampaignRequest updateRequest = new CreateCampaignRequest("Updated Name", "Updated Desc", gameMaster.getId());

        mockMvc.perform(put("/api/campaigns/" + campaign.getId())
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
                .gameMaster(gameMaster)
                .status(CampaignEntity.CampaignStatus.ACTIVE)
                .build();
        campaign = campaignRepository.save(campaign);

        mockMvc.perform(delete("/api/campaigns/" + campaign.getId()))
                .andExpect(status().isNoContent());

        assertThat(campaignRepository.existsById(campaign.getId())).isFalse();
    }
}