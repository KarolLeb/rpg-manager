package com.rpgmanager.backend.session;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.backend.config.SecurityConfig;
import com.rpgmanager.backend.config.SecurityProperties;
import com.rpgmanager.common.security.BrowserNavigationFilter;
import com.rpgmanager.common.security.JwtFilter;
import com.rpgmanager.common.security.JwtUtil;
import java.time.OffsetDateTime;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SessionController.class)
@Import({
  SecurityConfig.class,
  SecurityProperties.class,
  JwtFilter.class,
  BrowserNavigationFilter.class
})
class SessionControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private SessionService sessionService;
  @MockitoBean private JwtUtil jwtUtil;
  @MockitoBean private UserDetailsService userDetailsService;

  @Test
  @WithMockUser
  void createSession_shouldReturnDTO() throws Exception {
    CreateSessionRequest request =
        new CreateSessionRequest(1L, "Session", "Desc", OffsetDateTime.now());
    SessionDto response = Instancio.create(SessionDto.class);
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
    SessionDto response = Instancio.create(SessionDto.class);
    when(sessionService.getSession(1L)).thenReturn(response);

    mockMvc
        .perform(get("/api/sessions/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(response.getId()));
  }

  @Test
  @WithMockUser
  void getSessionsByCampaign_shouldReturnList() throws Exception {
    List<SessionDto> response = Instancio.ofList(SessionDto.class).size(2).create();
    when(sessionService.getSessionsByCampaign(1L)).thenReturn(response);

    mockMvc
        .perform(get("/api/sessions/campaign/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  @WithMockUser
  void updateSession_shouldReturnDTO() throws Exception {
    CreateSessionRequest request =
        new CreateSessionRequest(1L, "Updated", "Desc", OffsetDateTime.now());
    SessionDto response = Instancio.create(SessionDto.class);
    response.setName("Updated");

    when(sessionService.updateSession(eq(1L), any())).thenReturn(response);

    mockMvc
        .perform(
            put("/api/sessions/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated"));
  }

  @Test
  @WithMockUser
  void cancelSession_shouldReturnOk() throws Exception {
    mockMvc.perform(post("/api/sessions/1/cancel").with(csrf())).andExpect(status().isOk());
    verify(sessionService).cancelSession(1L);
  }

  @Test
  @WithMockUser
  void completeSession_shouldReturnOk() throws Exception {
    mockMvc.perform(post("/api/sessions/1/complete").with(csrf())).andExpect(status().isOk());
    verify(sessionService).completeSession(1L);
  }
}
