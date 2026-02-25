package com.rpgmanager.backend.style;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rpgmanager.backend.config.SecurityConfig;
import com.rpgmanager.backend.config.SecurityProperties;
import com.rpgmanager.backend.errorlog.ErrorLogService;
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

@WebMvcTest(StyleController.class)
@Import({
    SecurityConfig.class,
    SecurityProperties.class,
    JwtFilter.class,
    BrowserNavigationFilter.class
})
class StyleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private StyleService styleService;

  @MockitoBean
  private ErrorLogService errorLogService;

  @MockitoBean
  private JwtUtil jwtUtil;

  @MockitoBean
  private UserDetailsService userDetailsService;

  @Test
  @WithMockUser
  void shouldGetAggregatedStyle() throws Exception {
    given(styleService.getAggregatedCss(1L)).willReturn(":root { --test: #abc; }");

    mockMvc
        .perform(get("/api/styles/aggregated").param("characterId", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/css;charset=UTF-8"))
        .andExpect(content().string(":root { --test: #abc; }"));
  }

  @Test
  @WithMockUser
  void shouldGetStyle() throws Exception {
    given(styleService.getStyle(StyleLevel.CAMPAIGN, "1")).willReturn("body { color: red; }");

    mockMvc
        .perform(get("/api/styles/CAMPAIGN/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/css;charset=UTF-8"))
        .andExpect(content().string("body { color: red; }"));
  }

  @Test
  @WithMockUser
  void shouldUpdateStyle() throws Exception {
    String css = "body { color: blue; }";

    mockMvc.perform(post("/api/styles/CAMPAIGN/1").content(css)).andExpect(status().isOk());

    verify(styleService).saveStyle(StyleLevel.CAMPAIGN, "1", css);
  }
}
