package com.rpgmanager.backend.style;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

@WebMvcTest(StyleController.class)
@Import(SecurityConfig.class)
class StyleControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private StyleService styleService;

  @MockBean private JwtUtil jwtUtil;

  @MockBean private UserDetailsService userDetailsService;

  @Test
  @WithMockUser
  void shouldGetStyle() throws Exception {
    given(styleService.getCssForRace("human")).willReturn("body { color: red; }");

    mockMvc
        .perform(get("/api/styles/human"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/css;charset=UTF-8"))
        .andExpect(content().string("body { color: red; }"));
  }

  @Test
  @WithMockUser
  void shouldUpdateStyle() throws Exception {
    String css = "body { color: blue; }";

    mockMvc.perform(post("/api/styles/human").content(css)).andExpect(status().isOk());

    verify(styleService).saveStyle("human", css);
  }
}
