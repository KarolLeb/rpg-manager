package com.rpgmanager.common.security;

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
  private final Long userId = 123L;
  private final String role = "PLAYER";
  private final String secret =
      "bardzo_dlugi_i_bezpieczny_klucz_do_podpisywania_tokenow_jwt_1234567890";

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
  }

  private String createToken(String sub, Long id, String r, Date exp) {
    return Jwts.builder()
        .subject(sub)
        .claim("userId", id)
        .claim("role", r)
        .issuedAt(new Date())
        .expiration(exp)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
        .compact();
  }

  @Test
  void shouldExtractUsername() {
    String token =
        createToken(username, userId, role, new Date(System.currentTimeMillis() + 10000));
    String extractedUsername = jwtUtil.extractUsername(token);
    assertEquals(username, extractedUsername);
  }

  @Test
  void shouldExtractUserId() {
    String token =
        createToken(username, userId, role, new Date(System.currentTimeMillis() + 10000));
    Long extractedUserId = jwtUtil.extractUserId(token);
    assertEquals(userId, extractedUserId);
  }

  @Test
  void shouldExtractRole() {
    String token =
        createToken(username, userId, role, new Date(System.currentTimeMillis() + 10000));
    String extractedRole = jwtUtil.extractRole(token);
    assertEquals(role, extractedRole);
  }

  @Test
  void shouldValidateToken() {
    String token =
        createToken(username, userId, role, new Date(System.currentTimeMillis() + 10000));
    assertTrue(jwtUtil.validateToken(token, username));
  }

  @Test
  void shouldNotValidateTokenForDifferentUser() {
    String token =
        createToken(username, userId, role, new Date(System.currentTimeMillis() + 10000));
    assertFalse(jwtUtil.validateToken(token, "otheruser"));
  }

  @Test
  void shouldNotValidateExpiredToken() {
    String token = createToken(username, userId, role, new Date(System.currentTimeMillis() - 1000));

    assertFalse(jwtUtil.validateToken(token, username));
  }

  @Test
  void shouldReturnFalseForInvalidToken() {
    assertFalse(jwtUtil.validateToken(null, username));
    assertFalse(jwtUtil.validateToken("", username));
    assertFalse(jwtUtil.validateToken("  ", username));
    assertFalse(jwtUtil.validateToken("invalid.token.here", username));
  }

  @Test
  void shouldGenerateToken() {
    String token = jwtUtil.generateToken(username, userId, role);
    assertNotNull(token);
    assertEquals(username, jwtUtil.extractUsername(token));
    assertEquals(userId, jwtUtil.extractUserId(token));
    assertEquals(role, jwtUtil.extractRole(token));
  }
}
