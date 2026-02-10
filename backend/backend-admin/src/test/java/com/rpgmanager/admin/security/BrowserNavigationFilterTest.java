package com.rpgmanager.admin.security;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class BrowserNavigationFilterTest {

  private BrowserNavigationFilter filter;

  @Mock private HttpServletRequest request;
  @Mock private HttpServletResponse response;
  @Mock private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    filter = new BrowserNavigationFilter();
  }

  @Test
  void shouldBlockTopLevelNavigation() throws ServletException, IOException {
    given(request.getRequestURI()).willReturn("/api/admin/users");
    given(request.getHeader("Sec-Fetch-Mode")).willReturn("navigate");
    StringWriter out = new StringWriter();
    given(response.getWriter()).willReturn(new PrintWriter(out));

    filter.doFilterInternal(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verifyNoInteractions(filterChain);
  }

  @Test
  void shouldAllowNonApiRequest() throws ServletException, IOException {
    given(request.getRequestURI()).willReturn("/index.html");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldAllowXhrRequest() throws ServletException, IOException {
    given(request.getRequestURI()).willReturn("/api/admin/users");
    given(request.getHeader("Sec-Fetch-Mode")).willReturn("cors");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldBlockIfAcceptHtml() throws ServletException, IOException {
    given(request.getRequestURI()).willReturn("/api/admin/users");
    given(request.getHeader("Accept")).willReturn("text/html,application/xhtml+xml");
    StringWriter out = new StringWriter();
    given(response.getWriter()).willReturn(new PrintWriter(out));

    filter.doFilterInternal(request, response, filterChain);

    verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
  }

  @Test
  void shouldAllowIfAcceptJson() throws ServletException, IOException {
    given(request.getRequestURI()).willReturn("/api/admin/users");
    given(request.getHeader("Accept")).willReturn("application/json");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldAllowIfAcceptHtmlAndJson() throws ServletException, IOException {
    given(request.getRequestURI()).willReturn("/api/admin/users");
    given(request.getHeader("Accept")).willReturn("text/html,application/json");

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }
}
