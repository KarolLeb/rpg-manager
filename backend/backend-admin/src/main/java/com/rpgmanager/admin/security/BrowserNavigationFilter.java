package com.rpgmanager.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to prevent manual access to API endpoints from a browser tab.
 */
@Component
public class BrowserNavigationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String path = request.getRequestURI();
    String fetchMode = request.getHeader("Sec-Fetch-Mode");
    String accept = request.getHeader("Accept");

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
    if ("navigate".equalsIgnoreCase(fetchMode)) {
      return true;
    }
    return accept != null && accept.contains("text/html") && !accept.contains("application/json");
  }
}
