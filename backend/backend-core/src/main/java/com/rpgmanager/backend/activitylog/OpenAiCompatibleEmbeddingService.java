package com.rpgmanager.backend.activitylog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmbeddingService that uses an OpenAI-compatible API (like
 * Ollama).
 */
@Service
@ConditionalOnProperty(name = "rpg.embeddings.provider", havingValue = "openai")
@RequiredArgsConstructor
@Slf4j
public class OpenAiCompatibleEmbeddingService implements EmbeddingService {

    private final OpenAiEmbeddingClient client;

    @Value("${rpg.embeddings.api-key:none}")
    private String apiKey;

    @Value("${rpg.embeddings.model:nomic-embed-text}")
    private String model;

    @Override
    public float[] generateEmbedding(String text) {
        log.debug("Generating embedding for text using OpenAI-compatible API: {}", text);

        String authHeader = "Bearer " + apiKey;
        OpenAiEmbeddingRequest request = OpenAiEmbeddingRequest.builder()
                .input(text)
                .model(model)
                .build();

        try {
            OpenAiEmbeddingResponse response = client.getEmbeddings(authHeader, request);
            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                return response.getData().get(0).getEmbedding();
            }
            throw new RuntimeException("Empty or null response from embedding API");
        } catch (Exception e) {
            log.error("Failed to generate embedding via API: {}", e.getMessage());
            throw new RuntimeException("Embedding generation failed", e);
        }
    }
}
