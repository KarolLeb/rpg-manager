package com.rpgmanager.backend.campaign.infrastructure.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.backend.campaign.application.dto.CampaignDto;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;
import com.rpgmanager.backend.campaign.application.port.in.CreateCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.DeleteCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.GetCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.UpdateCampaignUseCase;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.security.JwtUtil;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CampaignController.class)
class CampaignControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private CreateCampaignUseCase createCampaignUseCase;
  @MockitoBean private GetCampaignUseCase getCampaignUseCase;
  @MockitoBean private UpdateCampaignUseCase updateCampaignUseCase;
  @MockitoBean private DeleteCampaignUseCase deleteCampaignUseCase;
  @MockitoBean private JwtUtil jwtUtil;
  @MockitoBean private UserDetailsService userDetailsService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldGetAllCampaigns() throws Exception {
    CampaignDto dto =
        new CampaignDto(
            1L,
            "Test Campaign",
            "Desc",
            OffsetDateTime.now(),
            CampaignDomain.CampaignStatus.ACTIVE,
            1L,
            "GM");
    given(getCampaignUseCase.getAllCampaigns()).willReturn(List.of(dto));

    mockMvc
        .perform(get("/api/campaigns").with(user("user")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Test Campaign"));
  }

  @Test
  void shouldGetCampaignById() throws Exception {
    CampaignDto dto =
        new CampaignDto(
            1L,
            "Test Campaign",
            "Desc",
            OffsetDateTime.now(),
            CampaignDomain.CampaignStatus.ACTIVE,
            1L,
            "GM");
    given(getCampaignUseCase.getCampaignById(1L)).willReturn(dto);

    mockMvc
        .perform(get("/api/campaigns/1").with(user("user")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Campaign"));
  }

  @Test
  void shouldCreateCampaign() throws Exception {
    CreateCampaignRequest request = new CreateCampaignRequest("New", "Desc", 1L);
    CampaignDto response =
        new CampaignDto(
            1L,
            "New",
            "Desc",
            OffsetDateTime.now(),
            CampaignDomain.CampaignStatus.ACTIVE,
            1L,
            "GM");

    given(createCampaignUseCase.createCampaign(any())).willReturn(response);

    mockMvc
        .perform(
            post("/api/campaigns")
                .with(csrf())
                .with(user("user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New"));
  }

  @Test
  void shouldUpdateCampaign() throws Exception {
    CreateCampaignRequest request = new CreateCampaignRequest("Updated", "Desc", 1L);
    CampaignDto response =
        new CampaignDto(
            1L,
            "Updated",
            "Desc",
            OffsetDateTime.now(),
            CampaignDomain.CampaignStatus.ACTIVE,
            1L,
            "GM");

    given(updateCampaignUseCase.updateCampaign(eq(1L), any())).willReturn(response);

    mockMvc
        .perform(
            put("/api/campaigns/1")
                .with(csrf())
                .with(user("user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"));
  }

  @Test
  void shouldDeleteCampaign() throws Exception {
    doNothing().when(deleteCampaignUseCase).deleteCampaign(1L);

    mockMvc
        .perform(delete("/api/campaigns/1").with(csrf()).with(user("user")))
        .andExpect(status().isNoContent());
  }
}
