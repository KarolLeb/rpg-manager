package com.rpgmanager.admin.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

  private JwtUtil jwtUtil;
  private final String secret = "mySecretKeyForTestingPurposesMustBeLongEnough1234567890";

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "secret", secret);
  }

  @Test
  void shouldExtractUsername() {
    String token =
        io.jsonwebtoken.Jwts.builder()
            .subject("testuser")
            .signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                    secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
            .compact();

    assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
  }

  @Test
  void shouldExtractUserId() {
    String token =
        io.jsonwebtoken.Jwts.builder()
            .claim("userId", 1L)
            .signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                    secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
            .compact();

    assertThat(jwtUtil.extractUserId(token)).isEqualTo(1L);
  }

  @Test
  void shouldExtractRole() {
    String token =
        io.jsonwebtoken.Jwts.builder()
            .claim("role", "ADMIN")
            .signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                    secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
            .compact();

    assertThat(jwtUtil.extractRole(token)).isEqualTo("ADMIN");
  }

  @Test
  void validateToken_shouldReturnTrue_whenValid() {
    String username = "testuser";
    String token =
        io.jsonwebtoken.Jwts.builder()
            .subject(username)
            .signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                    secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
            .compact();

    boolean isValid = jwtUtil.validateToken(token, username);

    assertThat(isValid).isTrue();
  }

  @Test
  void validateToken_shouldReturnFalse_whenUsernameMismatch() {
    String token =
        io.jsonwebtoken.Jwts.builder()
            .subject("testuser")
            .signWith(
                io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                    secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)))
            .compact();

    boolean isValid = jwtUtil.validateToken(token, "otheruser");

    assertThat(isValid).isFalse();
  }

  @Test
  void validateToken_shouldReturnFalse_whenTokenInvalid() {
    boolean isValid = jwtUtil.validateToken("invalid-token", "testuser");

    assertThat(isValid).isFalse();
  }
}
