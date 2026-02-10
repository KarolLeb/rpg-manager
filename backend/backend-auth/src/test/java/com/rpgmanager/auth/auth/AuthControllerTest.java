package com.rpgmanager.auth.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rpgmanager.auth.config.SecurityConfig;
import com.rpgmanager.auth.config.SecurityProperties;
import com.rpgmanager.auth.security.BrowserNavigationFilter;
import com.rpgmanager.auth.security.JwtFilter;
import com.rpgmanager.auth.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import({
  SecurityConfig.class,
  SecurityProperties.class,
  JwtFilter.class,
  BrowserNavigationFilter.class
})
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AuthService authService;

  @MockitoBean private JwtUtil jwtUtil;

  @MockitoBean private UserDetailsService userDetailsService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void shouldLogin() throws Exception {
    LoginRequest request = new LoginRequest("testuser", "password");
    AuthResponse response = new AuthResponse("token", "testuser", "USER");
    given(authService.login(any(LoginRequest.class))).willReturn(response);

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(response)));
  }

  @Test
  void shouldRegister() throws Exception {
    RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password");

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().string("User registered successfully"));

    verify(authService).register(any(RegisterRequest.class));
  }
}
