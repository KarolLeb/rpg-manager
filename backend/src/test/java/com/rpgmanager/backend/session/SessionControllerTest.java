package com.rpgmanager.backend.session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.backend.security.JwtUtil;
import java.time.OffsetDateTime;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SessionController.class)
class SessionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private SessionService sessionService;
  @MockitoBean private JwtUtil jwtUtil;

  @Test
  @WithMockUser
  void createSession_shouldReturnDTO() throws Exception {
    CreateSessionRequest request =
        new CreateSessionRequest(1L, "Session", "Desc", OffsetDateTime.now());
    SessionDTO response = Instancio.create(SessionDTO.class);
    response.setName("Session");

    when(sessionService.createSession(any())).thenReturn(response);

    mockMvc
        .perform(
            post("/api/sessions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Session"));
  }

  @Test
  @WithMockUser
  void getSession_shouldReturnDTO() throws Exception {
    SessionDTO response = Instancio.create(SessionDTO.class);
    when(sessionService.getSession(1L)).thenReturn(response);

    mockMvc.perform(get("/api/sessions/1")).andExpect(status().isOk());
  }
}
