package com.rpgmanager.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to prevent manual access to API endpoints from a browser tab. It
 * blocks requests that
 * appear to be top-level navigation (e.g. typing URL in address bar).
 */
@Component
public class BrowserNavigationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      @org.springframework.lang.NonNull HttpServletRequest request,
      @org.springframework.lang.NonNull HttpServletResponse response,
      @org.springframework.lang.NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getRequestURI();
    String fetchMode = request.getHeader("Sec-Fetch-Mode");
    String accept = request.getHeader("Accept");

    // Block if it's an API call triggered by top-level browser navigation
    // Browsers send 'navigate' for address bar entry.
    if (path.startsWith("/api") && isBrowserTabRequest(fetchMode, accept)) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json");
      response
          .getWriter()
          .write("{\"error\": \"API access is restricted to the web application.\"}");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private boolean isBrowserTabRequest(String fetchMode, String accept) {
    // 'navigate' mode is a definitive sign of browser tab navigation
    if ("navigate".equalsIgnoreCase(fetchMode)) {
      return true;
    }
    // Fallback: browser tab navigation always prefers text/html
    return accept != null && accept.contains("text/html") && !accept.contains("application/json");
  }
}
