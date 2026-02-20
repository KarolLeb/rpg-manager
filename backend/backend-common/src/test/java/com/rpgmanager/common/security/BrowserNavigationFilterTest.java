package com.rpgmanager.common.security;

import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BrowserNavigationFilterTest {

  private BrowserNavigationFilter filter;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private FilterChain filterChain;
  private StringWriter responseWriter;

  @BeforeEach
  void setUp() throws IOException {
    filter = new BrowserNavigationFilter();
    request = mock(HttpServletRequest.class);
    response = mock(HttpServletResponse.class);
    filterChain = mock(FilterChain.class);
    responseWriter = new StringWriter();
    when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @Test
  void shouldContinueChainWhenPathDoesNotStartWithApi() throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/index.html");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(anyInt());
  }

  @Test
  void shouldContinueChainWhenApiCallNotFromBrowserTab() throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/api/data");
    when(request.getHeader("Sec-Fetch-Mode")).thenReturn("cors");
    when(request.getHeader("Accept")).thenReturn("application/json");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
    verify(response, never()).setStatus(anyInt());
  }

  @ParameterizedTest
  @CsvSource({
    "navigate, application/json",
    "NAVIGATE, text/html",
    "none, text/html",
    ", text/html"
  })
  void shouldBlockWhenApiCallFromBrowserTab(String fetchMode, String accept)
      throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/api/secret");
    when(request.getHeader("Sec-Fetch-Mode")).thenReturn(fetchMode);
    when(request.getHeader("Accept")).thenReturn(accept);

    filter.doFilterInternal(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(response).setContentType("application/json");
    verify(filterChain, never()).doFilter(any(), any());
  }

  @Test
  void shouldContinueChainWhenAcceptContainsJsonEvenWithHtml()
      throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/api/data");
    when(request.getHeader("Sec-Fetch-Mode")).thenReturn("cors");
    when(request.getHeader("Accept")).thenReturn("text/html,application/json");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldContinueChainWhenAcceptIsNullAndNotNavigate() throws ServletException, IOException {
    when(request.getRequestURI()).thenReturn("/api/data");
    when(request.getHeader("Sec-Fetch-Mode")).thenReturn("cors");
    when(request.getHeader("Accept")).thenReturn(null);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }
}
