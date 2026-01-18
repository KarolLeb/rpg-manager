package com.rpgmanager.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String username = "testuser";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
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
}
