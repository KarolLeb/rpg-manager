package com.rpgmanager.auth.user.infrastructure.adapter.incoming.rest;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.rpgmanager.auth.config.SecurityConfig;
import com.rpgmanager.auth.config.SecurityProperties;
import com.rpgmanager.common.security.BrowserNavigationFilter;
import com.rpgmanager.common.security.JwtFilter;
import com.rpgmanager.common.security.JwtUtil;
import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.domain.repository.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@Import({
  SecurityConfig.class,
  SecurityProperties.class,
  JwtFilter.class,
  BrowserNavigationFilter.class
})
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserRepositoryPort userRepository;
  @MockitoBean private JwtUtil jwtUtil;
  @MockitoBean private UserDetailsService userDetailsService;

  @Test
  void getUserById_shouldReturnUser_whenExists() throws Exception {
    UserDomain user = new UserDomain();
    user.setId(1L);
    user.setUsername("testuser");
    given(userRepository.findById(1L)).willReturn(Optional.of(user));

    mockMvc
        .perform(get("/api/users/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("testuser"));
  }

  @Test
  void getUserById_shouldReturnNotFound_whenDoesNotExist() throws Exception {
    given(userRepository.findById(1L)).willReturn(Optional.empty());

    mockMvc.perform(get("/api/users/1")).andExpect(status().isNotFound());
  }

  @Test
  void getUserByUsername_shouldReturnUser_whenExists() throws Exception {
    UserDomain user = new UserDomain();
    user.setUsername("testuser");
    given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

    mockMvc
        .perform(get("/api/users").param("username", "testuser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("testuser"));
  }

  @Test
  void getUserByUsername_shouldReturnNotFound_whenDoesNotExist() throws Exception {
    given(userRepository.findByUsername("testuser")).willReturn(Optional.empty());

    mockMvc
        .perform(get("/api/users").param("username", "testuser"))
        .andExpect(status().isNotFound());
  }

  @Test
  void getAllUsers_shouldReturnList() throws Exception {
    UserDomain user = new UserDomain();
    user.setUsername("testuser");
    given(userRepository.findAll()).willReturn(List.of(user));

    mockMvc
        .perform(get("/api/users/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("testuser"));
  }
}
