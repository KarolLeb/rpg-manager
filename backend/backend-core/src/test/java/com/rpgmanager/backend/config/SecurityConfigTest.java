package com.rpgmanager.backend.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.rpgmanager.common.security.BrowserNavigationFilter;
import com.rpgmanager.common.security.JwtFilter;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecurityConfigTest {

  private final JwtFilter jwtFilter = mock(JwtFilter.class);
  private final BrowserNavigationFilter browserNavigationFilter = new BrowserNavigationFilter();
  private final SecurityConfig securityConfig =
      new SecurityConfig(jwtFilter, browserNavigationFilter);

  @Test
  void passwordEncoder_shouldBeBCrypt() {
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    assertThat(encoder).isNotNull();
    assertThat(encoder.encode("password")).startsWith("$2a$");
  }

  @Test
  void authenticationManager_shouldReturnFromConfig() throws Exception {
    AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
    securityConfig.authenticationManager(authConfig);
    // Verifying it calls the underlying config
    org.mockito.Mockito.verify(authConfig).getAuthenticationManager();
  }
}
