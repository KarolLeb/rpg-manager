package com.rpgmanager.backend.security;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

  private JwtUtil jwtUtil;
  private final String username = "testuser";
  private final String secret =
      "bardzo_dlugi_i_bezpieczny_klucz_do_podpisywania_tokenow_jwt_1234567890";

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
  }

  @Test
  void shouldGenerateToken() {
    String token = jwtUtil.generateToken(username);
    assertNotNull(token);
  }

  @Test
  void shouldExtractUsername() {
    String token = jwtUtil.generateToken(username);
    String extractedUsername = jwtUtil.extractUsername(token);
    assertEquals(username, extractedUsername);
  }

  @Test
  void shouldValidateToken() {
    String token = jwtUtil.generateToken(username);
    assertTrue(jwtUtil.validateToken(token, username));
  }

  @Test
  void shouldNotValidateTokenForDifferentUser() {
    String token = jwtUtil.generateToken(username);
    assertFalse(jwtUtil.validateToken(token, "otheruser"));
  }

  @Test
  void shouldNotValidateExpiredToken() {
    String token =
        Jwts.builder()
            .subject(username)
            .issuedAt(new Date(System.currentTimeMillis() - 10000))
            .expiration(new Date(System.currentTimeMillis() - 1000)) // Expired
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
            .compact();

    assertThrows(
        io.jsonwebtoken.ExpiredJwtException.class, () -> jwtUtil.validateToken(token, username));
  }

  @Test
  void shouldThrowExceptionWhenTokenIsExpired() {
    String token =
        Jwts.builder()
            .subject(username)
            .expiration(new Date(System.currentTimeMillis() - 1000))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
            .compact();

    assertThrows(
        io.jsonwebtoken.ExpiredJwtException.class,
        () -> ReflectionTestUtils.invokeMethod(jwtUtil, "isTokenExpired", token));
  }
}
