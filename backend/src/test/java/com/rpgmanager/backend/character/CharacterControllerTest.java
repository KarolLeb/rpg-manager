package com.rpgmanager.backend.character;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.backend.character.dto.CharacterResponse;
import com.rpgmanager.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
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
    private CharacterRepository characterRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllCharactersAsResponses() throws Exception {
        Character character = new Character();
        character.setId(1L);
        character.setUuid(UUID.randomUUID());
        character.setName("Test Char");
        character.setCharacterClass("Warrior");
        character.setLevel(5);
        character.setStats("Str: 10");

        given(characterRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .willReturn(List.of(character));

        mockMvc.perform(get("/api/characters")
                        .with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Char"))
                .andExpect(jsonPath("$[0].characterClass").value("Warrior"));
    }

    @Test
    void shouldUpdateCharacterAndReturnResponse() throws Exception {
        Long charId = 1L;
        Character existingCharacter = new Character();
        existingCharacter.setId(charId);
        existingCharacter.setUuid(UUID.randomUUID());
        existingCharacter.setName("Old Name");
        existingCharacter.setLevel(1);

        Character updateRequest = new Character();
        updateRequest.setName("New Name");
        updateRequest.setLevel(2);
        updateRequest.setStats("Str: 12");

        given(characterRepository.findById(charId)).willReturn(Optional.of(existingCharacter));
        given(characterRepository.save(any(Character.class))).willAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/api/characters/{id}", charId)
                .with(csrf())
                .with(user("user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.level").value(2));
    }
}
