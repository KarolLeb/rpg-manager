package com.rpgmanager.backend.character;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.port.in.GetCharacterUseCase;
import com.rpgmanager.backend.character.application.port.in.JoinCampaignUseCase;
import com.rpgmanager.backend.character.application.port.in.UpdateCharacterUseCase;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.infrastructure.adapter.in.web.CharacterController;
import com.rpgmanager.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@WebMvcTest(CharacterController.class)
class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetCharacterUseCase getCharacterUseCase;

    @MockBean
    private UpdateCharacterUseCase updateCharacterUseCase;

    @MockBean
    private JoinCampaignUseCase joinCampaignUseCase;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllCharactersAsResponses() throws Exception {
        CharacterResponse response = new CharacterResponse(
                UUID.randomUUID(),
                "Test Char",
                "Warrior",
                5,
                "Str: 10",
                "Owner",
                "Campaign",
                "PERMANENT"
        );

        given(getCharacterUseCase.getAllCharacters()).willReturn(List.of(response));

        mockMvc.perform(get("/api/characters")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Char"))
                .andExpect(jsonPath("$[0].characterClass").value("Warrior"));
    }

    @Test
    void shouldUpdateCharacterAndReturnResponse() throws Exception {
        UUID charId = UUID.randomUUID();
        CharacterDomain updateRequest = CharacterDomain.builder()
                .name("New Name")
                .level(2)
                .stats("Str: 12")
                .build();

        CharacterResponse response = new CharacterResponse(
                charId,
                "New Name",
                "Warrior",
                2,
                "Str: 12",
                "Owner",
                "Campaign",
                "PERMANENT"
        );

        given(updateCharacterUseCase.updateCharacter(eq(charId), any(CharacterDomain.class))).willReturn(response);

        mockMvc.perform(put("/api/characters/{id}", charId)
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
        UUID charId = UUID.randomUUID();
        CharacterDomain updateRequest = CharacterDomain.builder()
                .name("Ghost")
                .build();

        given(updateCharacterUseCase.updateCharacter(eq(charId), any(CharacterDomain.class)))
                .willThrow(new RuntimeException("Character not found"));

        String content = objectMapper.writeValueAsString(updateRequest);
        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            mockMvc.perform(put("/api/characters/{id}", charId)
                            .with(csrf())
                            .with(user("user"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content));
        });
    }
}