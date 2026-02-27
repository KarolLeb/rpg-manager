package com.rpgmanager.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Filter for processing JWT authentication tokens. */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(
      @org.springframework.lang.NonNull HttpServletRequest request,
      @org.springframework.lang.NonNull HttpServletResponse response,
      @org.springframework.lang.NonNull FilterChain filterChain)
      throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String username;
    final Long userId;
    final java.util.List<String> roles;

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      jwt = authHeader.substring(7);
      username = jwtUtil.extractUsername(jwt);
      userId = jwtUtil.extractUserId(jwt);
      roles = jwtUtil.extractRoles(jwt);

      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserContext userContext = new UserContext(
            username,
            "", // No password needed for stateless JWT
            roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList(),
            userId);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userContext, null, userContext.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    } catch (Exception e) {
      // Ignore invalid tokens
    }
    filterChain.doFilter(request, response);
  }
}
