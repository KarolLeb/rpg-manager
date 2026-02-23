package com.rpgmanager.backend.activitylog;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.backend.config.SecurityProperties;
import com.rpgmanager.common.security.JwtUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ActivityLogController.class)
@AutoConfigureMockMvc(addFilters = false)
class ActivityLogControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private ActivityLogService activityLogService;
  @MockitoBean private SecurityProperties securityProperties;
  @MockitoBean private JwtUtil jwtUtil;

  private ActivityLogDto activityDto;

  @BeforeEach
  void setUp() {
    activityDto =
        ActivityLogDto.builder()
            .id(1L)
            .description("Test activity")
            .actionType(ActivityLogEntry.ActionType.SESSION_START)
            .build();
  }

  @Test
  void shouldLogActivity() throws Exception {
    CreateActivityLogRequest request = new CreateActivityLogRequest();
    request.setDescription("Test activity");
    request.setActionType(ActivityLogEntry.ActionType.SESSION_START);

    when(activityLogService.logActivity(any())).thenReturn(activityDto);

    mockMvc
        .perform(
            post("/api/activity-log")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.description").value("Test activity"));
  }

  @Test
  void shouldSearchActivities() throws Exception {
    when(activityLogService.searchActivities(anyString(), anyInt())).thenReturn(List.of(activityDto));

    mockMvc
        .perform(get("/api/activity-log/search").param("q", "test").param("limit", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void shouldGetBySession() throws Exception {
    when(activityLogService.getActivitiesBySession(any())).thenReturn(List.of(activityDto));

    mockMvc
        .perform(get("/api/activity-log/session/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));
  }

  @Test
  void shouldGetByCampaign() throws Exception {
    when(activityLogService.getActivitiesByCampaign(any())).thenReturn(List.of(activityDto));

    mockMvc
        .perform(get("/api/activity-log/campaign/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1));
  }
}
