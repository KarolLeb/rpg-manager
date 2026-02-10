package com.rpgmanager.admin.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class JwtFilterTest {

  private JwtFilter jwtFilter;

  @Mock private JwtUtil jwtUtil;
  @Mock private UserDetailsService userDetailsService;
  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

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
  void shouldAuthenticateIfTokenIsValid() throws ServletException, IOException {
    String token = "valid-token";
    String username = "testuser";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(username);
    given(jwtUtil.validateToken(token, username)).willReturn(true);

    UserDetails userDetails =
        new User(
            username,
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(username);
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
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
      }

  @Test
  void shouldNotAuthenticateIfTokenInvalid() throws ServletException, IOException {
    String token = "invalid-token";
    String username = "testuser";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willReturn(username);
    given(jwtUtil.validateToken(token, username)).willReturn(false);

    UserDetails userDetails =
        new User(
            username,
            "password",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
    given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }

  @Test
  void shouldHandleExceptionInFilter() throws ServletException, IOException {
    String token = "error-token";
    given(request.getHeader("Authorization")).willReturn("Bearer " + token);
    given(jwtUtil.extractUsername(token)).willThrow(new RuntimeException("JWT error"));

    jwtFilter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }
}
