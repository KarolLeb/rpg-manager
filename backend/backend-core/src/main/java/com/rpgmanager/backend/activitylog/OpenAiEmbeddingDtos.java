package com.rpgmanager.backend.activitylog;

import java.util.List;
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

/** Response DTO for OpenAI-compatible embedding API. */
@Value
@Builder
@Jacksonized
public class OpenAiEmbeddingResponse {
    List<Data> data;

    @Value
    @Builder
    @Jacksonized
    public static class Data {
        float[] embedding;
    }
}
