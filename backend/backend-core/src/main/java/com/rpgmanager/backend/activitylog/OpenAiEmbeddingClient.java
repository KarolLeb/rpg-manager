package com.rpgmanager.backend.activitylog;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/** Feign client for OpenAI-compatible embedding service (like Ollama or OpenAI). */
@FeignClient(name = "openai-embedding-client", url = "${rpg.embeddings.url}")
public interface OpenAiEmbeddingClient {

  @PostMapping
  OpenAiEmbeddingResponse getEmbeddings(
      @RequestHeader("Authorization") String authHeader,
      @RequestBody OpenAiEmbeddingRequest request);
}
