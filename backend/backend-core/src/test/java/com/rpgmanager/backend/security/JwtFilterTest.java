package com.rpgmanager.backend.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class JwtFilterTest {

  private JwtFilter jwtFilter;

  @Mock private JwtUtil jwtUtil;

  @Mock private UserDetailsService userDetailsService;

  @Mock private HttpServletRequest request;

  @Mock private HttpServletResponse response;

  @Mock private FilterChain filterChain;

  @Mock private UserDetails userDetails;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    jwtFilter = new JwtFilter(jwtUtil, userDetailsService);
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
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(username);
    given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);
    given(userDetails.getUsername()).willReturn(username);
    given(jwtUtil.validateToken(token, username)).willReturn(true);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    org.springframework.security.core.Authentication auth =
        SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getDetails()).isNotNull();
  }

  @Test
  void shouldNotAuthenticateIfTokenIsInvalid() throws ServletException, IOException {
    String token = "invalid-token";
    String username = "testuser";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(username);
    given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);
    given(userDetails.getUsername()).willReturn(username);
    given(jwtUtil.validateToken(token, username)).willReturn(false);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assert SecurityContextHolder.getContext().getAuthentication() == null;
  }

  @Test
  void shouldNotAuthenticateIfUserAlreadyAuthenticated() throws ServletException, IOException {
    String token = "valid-token";
    String username = "testuser";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(username);

    SecurityContextHolder.getContext()
        .setAuthentication(mock(org.springframework.security.core.Authentication.class));

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
  }

  @Test
  void shouldNotAuthenticateIfUsernameIsNull() throws ServletException, IOException {
    String token = "valid-token";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(null);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(userDetailsService, never()).loadUserByUsername(anyString());
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
