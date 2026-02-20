package com.rpgmanager.backend.dice;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rpgmanager.backend.config.SecurityConfig;
import com.rpgmanager.backend.config.SecurityProperties;
import com.rpgmanager.common.security.BrowserNavigationFilter;
import com.rpgmanager.common.security.JwtFilter;
import com.rpgmanager.common.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DiceController.class)
@Import({
  SecurityConfig.class,
  SecurityProperties.class,
  JwtFilter.class,
  BrowserNavigationFilter.class
})
class DiceControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DiceService diceService;
  @MockitoBean private JwtUtil jwtUtil;
  @MockitoBean private UserDetailsService userDetailsService;

  @Test
  @WithMockUser
  void roll_shouldReturnResult() throws Exception {
    when(diceService.roll(20)).thenReturn(15);

    mockMvc
        .perform(get("/api/dice/roll/20"))
        .andExpect(status().isOk())
        .andExpect(content().string("15"));
  }
}
