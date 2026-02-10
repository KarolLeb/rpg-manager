package com.rpgmanager.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Utility class for handling JWT tokens. */
@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private long expiration;

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generates a JWT token for a given user.
   *
   * @param username the username
   * @param userId the user ID
   * @param role the user role
   * @return the generated token
   */
  public String generateToken(String username, Long userId, String role) {
    return Jwts.builder()
        .subject(username)
        .claim("userId", userId)
        .claim("role", role)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * Extracts the user ID from a JWT token.
   *
   * @param token the JWT token
   * @return the user ID
   */
  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }

  /**
   * Extracts the role from a JWT token.
   *
   * @param token the JWT token
   * @return the role
   */
  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  /**
   * Extracts the username from a JWT token.
   *
   * @param token the JWT token
   * @return the username
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extracts a specific claim from a JWT token.
   *
   * @param token the JWT token
   * @param claimsResolver the function to resolve the claim
   * @param <T> the type of the claim
   * @return the claim value
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }

  /**
   * Validates a JWT token against a username.
   *
   * @param token the JWT token
   * @param username the username to validate against
   * @return true if the token is valid, false otherwise
   */
  public boolean validateToken(String token, String username) {
    try {
      String extractedUsername = extractUsername(token);
      return extractedUsername.equals(username);
    } catch (Exception e) {
      return false;
    }
  }
}
