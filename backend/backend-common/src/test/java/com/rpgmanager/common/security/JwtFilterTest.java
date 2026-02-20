package com.rpgmanager.common.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;

class JwtFilterTest {

  private JwtFilter jwtFilter;

  @Mock private JwtUtil jwtUtil;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    jwtFilter = new JwtFilter(jwtUtil);
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldContinueChainIfNoAuthHeader() throws ServletException, IOException {
    given(request.getHeader("Authorization")).willReturn(null);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtUtil);
  }

  @Test
  void shouldContinueChainIfAuthHeaderDoesNotStartWithBearer()
      throws ServletException, IOException {
    given(request.getHeader("Authorization")).willReturn("Basic 123");

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verifyNoInteractions(jwtUtil);
  }

  @Test
  void shouldAuthenticateIfTokenIsValid() throws ServletException, IOException {
    String token = "valid-token";
    String username = "testuser";
    Long userId = 1L;
    String role = "PLAYER";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(username);
    given(jwtUtil.extractUserId(token)).willReturn(userId);
    given(jwtUtil.extractRole(token)).willReturn(role);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    org.springframework.security.core.Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(((UserContext) auth.getPrincipal()).getUsername()).isEqualTo(username);
    assertThat(((UserContext) auth.getPrincipal()).getUserId()).isEqualTo(userId);
  }

  @Test
  void shouldNotAuthenticateIfUserAlreadyAuthenticated() throws ServletException, IOException {
    String token = "valid-token";
    String username = "testuser";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(username);

    org.springframework.security.core.Authentication existingAuth =
        mock(org.springframework.security.core.Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(existingAuth);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(existingAuth);
  }

  @Test
  void shouldNotAuthenticateIfUsernameIsNull() throws ServletException, IOException {
    String token = "valid-token";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(null);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldContinueChainWhenExceptionOccurs() throws ServletException, IOException {
    String token = "malformed-token";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willThrow(new RuntimeException("Parsing error"));

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assert SecurityContextHolder.getContext().getAuthentication() == null;
  }
}
