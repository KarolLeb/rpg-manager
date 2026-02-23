package com.rpgmanager.backend.activitylog;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/** Request DTO for OpenAI-compatible embedding API. */
@Value
@Builder
@Jacksonized
public class OpenAiEmbeddingRequest {
  String input;
  String model;
}
