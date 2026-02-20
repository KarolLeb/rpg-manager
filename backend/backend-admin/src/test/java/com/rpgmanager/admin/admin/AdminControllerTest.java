package com.rpgmanager.admin.admin;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.rpgmanager.admin.config.SecurityConfig;
import com.rpgmanager.admin.config.SecurityProperties;
import com.rpgmanager.common.security.BrowserNavigationFilter;
import com.rpgmanager.common.security.JwtFilter;
import com.rpgmanager.common.security.JwtUtil;
import com.rpgmanager.admin.user.domain.model.UserDomain;
import com.rpgmanager.admin.user.domain.repository.UserRepositoryPort;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdminController.class)
@Import({
  SecurityConfig.class,
  SecurityProperties.class,
  JwtFilter.class,
  BrowserNavigationFilter.class
})
class AdminControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private UserRepositoryPort userRepository;

  @MockitoBean private JwtUtil jwtUtil;

  @MockitoBean private UserDetailsService userDetailsService;

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldReturnUsersWhenAdmin() throws Exception {
    UserDomain user = new UserDomain();
    user.setUsername("testuser");
    given(userRepository.findAll()).willReturn(List.of(user));

    mockMvc
        .perform(get("/api/admin/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("testuser"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void shouldReturnForbiddenWhenNotAdmin() throws Exception {
    mockMvc.perform(get("/api/admin/users")).andExpect(status().isForbidden());
  }

  @Test
  void shouldReturnUnauthorizedWhenNotLoggedIn() throws Exception {
    mockMvc.perform(get("/api/admin/users")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void shouldReturnHealth() throws Exception {
    mockMvc
        .perform(get("/api/admin/health"))
        .andExpect(status().isOk())
        .andExpect(content().string("Admin module is healthy"));
  }
}
