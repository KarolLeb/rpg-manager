package com.rpgmanager.backend.dice;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rpgmanager.backend.config.SecurityConfig;
import com.rpgmanager.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DiceController.class)
@Import(SecurityConfig.class)
class DiceControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private DiceService diceService;

  @MockBean private JwtUtil jwtUtil;

  @MockBean private UserDetailsService userDetailsService;

  @Test
  @WithMockUser
  void shouldRollDice() throws Exception {
    given(diceService.roll(20)).willReturn(15);

    mockMvc
        .perform(get("/api/dice/roll/20"))
        .andExpect(status().isOk())
        .andExpect(content().string("15"));
  }
}
