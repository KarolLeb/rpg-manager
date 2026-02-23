package com.rpgmanager.backend.character;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.port.in.GetCharacterUseCase;
import com.rpgmanager.backend.character.application.port.in.JoinCampaignUseCase;
import com.rpgmanager.backend.character.application.port.in.UpdateCharacterUseCase;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.infrastructure.adapter.in.web.CharacterController;
import com.rpgmanager.backend.config.SecurityConfig;
import com.rpgmanager.backend.config.SecurityProperties;
import com.rpgmanager.backend.errorlog.ErrorLogService;
import com.rpgmanager.common.security.BrowserNavigationFilter;
import com.rpgmanager.common.security.JwtFilter;
import com.rpgmanager.common.security.JwtUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CharacterController.class)
@Import({
  SecurityConfig.class,
  SecurityProperties.class,
  JwtFilter.class,
  BrowserNavigationFilter.class
})
class CharacterControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private GetCharacterUseCase getCharacterUseCase;

  @MockitoBean private UpdateCharacterUseCase updateCharacterUseCase;

  @MockitoBean private JoinCampaignUseCase joinCampaignUseCase;

  @MockitoBean private ErrorLogService errorLogService;

  @MockitoBean private JwtUtil jwtUtil;

  @MockitoBean private UserDetailsService userDetailsService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldReturnAllCharactersAsResponses() throws Exception {
    CharacterResponse response =
        new CharacterResponse(
            1L, "Test Char", "Warrior", 5, "Str: 10", "Owner", "Campaign", "PERMANENT");

    given(getCharacterUseCase.getAllCharacters()).willReturn(List.of(response));

    mockMvc
        .perform(get("/api/characters").with(user("user")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Test Char"))
        .andExpect(jsonPath("$[0].characterClass").value("Warrior"));
  }

  @Test
  void shouldReturnCharacterById() throws Exception {
    CharacterResponse response =
        new CharacterResponse(
            1L, "Test Char", "Warrior", 5, "Str: 10", "Owner", "Campaign", "PERMANENT");

    given(getCharacterUseCase.getCharacter(1L)).willReturn(response);

    mockMvc
        .perform(get("/api/characters/1").with(user("user")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Test Char"));
  }

  @Test
  void shouldUpdateCharacterAndReturnResponse() throws Exception {
    Long charId = 1L;
    CharacterDomain updateRequest =
        CharacterDomain.builder().name("New Name").level(2).stats("Str: 12").build();

    CharacterResponse response =
        new CharacterResponse(
            charId, "New Name", "Warrior", 2, "Str: 12", "Owner", "Campaign", "PERMANENT");

    given(updateCharacterUseCase.updateCharacter(eq(charId), any(CharacterDomain.class)))
        .willReturn(response);

    mockMvc
        .perform(
            put("/api/characters/{id}", charId)
                .with(csrf())
                .with(user("user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("New Name"))
        .andExpect(jsonPath("$.level").value(2));
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistentCharacter() throws Exception {
    Long charId = 1L;
    CharacterDomain updateRequest = CharacterDomain.builder().name("Ghost").build();

    given(updateCharacterUseCase.updateCharacter(eq(charId), any(CharacterDomain.class)))
        .willThrow(new RuntimeException("Character not found"));

    String content = objectMapper.writeValueAsString(updateRequest);
    mockMvc
        .perform(
            put("/api/characters/{id}", charId)
                .with(csrf())
                .with(user("user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void shouldJoinCampaign() throws Exception {
    CharacterResponse response =
        new CharacterResponse(
            1L, "Test Char", "Warrior", 5, "Str: 10", "Owner", "Campaign", "PERMANENT");

    given(joinCampaignUseCase.joinCampaign(1L, 1L)).willReturn(response);

    mockMvc
        .perform(post("/api/characters/1/join-campaign/1").with(csrf()).with(user("user")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.campaignName").value("Campaign"));
  }
}
