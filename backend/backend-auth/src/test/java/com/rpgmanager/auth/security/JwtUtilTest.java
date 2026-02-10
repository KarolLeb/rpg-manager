package com.rpgmanager.auth.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

  private JwtUtil jwtUtil;
  private final String secret = "mySecretKeyForTestingPurposesMustBeLongEnough1234567890";
  private final long expiration = 3600000;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "secret", secret);
    ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
  }

  @Test
  void shouldGenerateAndExtractToken() {
    String username = "testuser";
    Long userId = 1L;
    String role = "PLAYER";

    String token = jwtUtil.generateToken(username, userId, role);

    assertThat(token).isNotNull();
    assertThat(jwtUtil.extractUsername(token)).isEqualTo(username);
    assertThat(jwtUtil.extractUserId(token)).isEqualTo(userId);
    assertThat(jwtUtil.extractRole(token)).isEqualTo(role);
  }

  @Test
  void validateToken_shouldReturnTrue_whenValid() {
    String username = "testuser";
    String token = jwtUtil.generateToken(username, 1L, "PLAYER");

    boolean isValid = jwtUtil.validateToken(token, username);

    assertThat(isValid).isTrue();
  }

  @Test
  void validateToken_shouldReturnFalse_whenUsernameMismatch() {
    String token = jwtUtil.generateToken("testuser", 1L, "PLAYER");

    boolean isValid = jwtUtil.validateToken(token, "otheruser");

    assertThat(isValid).isFalse();
  }

  @Test
  void validateToken_shouldReturnFalse_whenTokenInvalid() {
    boolean isValid = jwtUtil.validateToken("invalid-token", "testuser");

    assertThat(isValid).isFalse();
  }

  @Test
  void shouldExtractClaims() {
    String token = jwtUtil.generateToken("testuser", 1L, "PLAYER");

    assertThat(jwtUtil.extractUserId(token)).isEqualTo(1L);
    assertThat(jwtUtil.extractRole(token)).isEqualTo("PLAYER");
  }

  @Test
  void validateToken_shouldReturnFalse_whenExceptionOccurs() {
    // Token with wrong signature or malformed
    String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dW1lciJ9.invalid-signature";
    boolean isValid = jwtUtil.validateToken(token, "testuser");
    assertThat(isValid).isFalse();
  }
}
