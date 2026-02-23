package com.rpgmanager.backend.activitylog;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * A lightweight, local embedding service that generates deterministic
 * 384-dimensional vectors from
 * text using hash-based feature extraction. This is a placeholder
 * implementation suitable for
 * development and testing. For production-quality semantic search, replace with
 * an OpenAI or Ollama
 * embedding provider.
 */
@Service
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
            for (int i = 0; i < DIMENSION && i < hash.length * 8; i++) {
                int byteIdx = (i / 8) % hash.length;
                int bitIdx = i % 8;
                if ((hash[byteIdx] & (1 << bitIdx)) != 0) {
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
