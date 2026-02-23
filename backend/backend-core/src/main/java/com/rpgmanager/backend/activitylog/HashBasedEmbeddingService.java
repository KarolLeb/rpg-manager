package com.rpgmanager.backend.activitylog;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Local hash-based embedding service for development and testing. Produces deterministic
 * 384-dimensional vectors from text input.
 */
@Service
@ConditionalOnProperty(
    name = "rpg.embeddings.provider",
    havingValue = "hash",
    matchIfMissing = true)
public class HashBasedEmbeddingService implements EmbeddingService {

  private static final int DIMENSION = 384;

  @Override
  public float[] embed(String text) {
    if (text == null || text.isBlank()) {
      return new float[DIMENSION];
    }

    float[] vector = new float[DIMENSION];
    String normalized = text.toLowerCase(Locale.ROOT).trim();
    String[] tokens = normalized.split("\\s+");

    for (String token : tokens) {
      byte[] hash = sha256(token);
      int limit = Math.min(DIMENSION, hash.length * 8);
      for (int i = 0; i < limit; i++) {
        int byteIdx = (i / 8) % hash.length;
        int bitIdx = i % 8;
        if (((hash[byteIdx] & 0xff) & (1 << bitIdx)) != 0) {
          vector[i % DIMENSION] += 1.0f;
        } else {
          vector[i % DIMENSION] -= 1.0f;
        }
      }
    }

    // L2-normalize the vector
    float norm = 0;
    for (float v : vector) {
      norm += v * v;
    }
    norm = (float) Math.sqrt(norm);
    if (norm > 0) {
      for (int i = 0; i < vector.length; i++) {
        vector[i] /= norm;
      }
    }

    return vector;
  }

  @Override
  public int getDimension() {
    return DIMENSION;
  }

  private byte[] sha256(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return digest.digest(input.getBytes(StandardCharsets.UTF_8));
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("SHA-256 algorithm not available", e);
    }
  }
}
