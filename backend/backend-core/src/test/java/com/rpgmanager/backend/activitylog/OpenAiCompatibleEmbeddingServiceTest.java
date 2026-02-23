package com.rpgmanager.backend.activitylog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OpenAiCompatibleEmbeddingServiceTest {

    @Mock
    private OpenAiEmbeddingClient client;

    @InjectMocks
    private OpenAiCompatibleEmbeddingService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "apiKey", "test-key");
        ReflectionTestUtils.setField(service, "model", "test-model");
    }

    @Test
    void generateEmbedding_shouldReturnEmbeddingFromClient() {
        float[] expectedEmbedding = new float[] { 0.1f, 0.2f, 0.3f };
        OpenAiEmbeddingResponse response = new OpenAiEmbeddingResponse(
                List.of(new OpenAiEmbeddingResponse.Data(expectedEmbedding)));

        when(client.getEmbeddings(eq("Bearer test-key"), any(OpenAiEmbeddingRequest.class)))
                .thenReturn(response);

        float[] actualEmbedding = service.generateEmbedding("test text");

        assertThat(actualEmbedding).isEqualTo(expectedEmbedding);
    }

    @Test
    void generateEmbedding_shouldThrowExceptionOnNullResponse() {
        when(client.getEmbeddings(anyString(), any(OpenAiEmbeddingRequest.class)))
                .thenReturn(null);

        assertThatThrownBy(() -> service.generateEmbedding("test text"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empty or null response");
    }

    @Test
    void generateEmbedding_shouldThrowExceptionOnEmptyData() {
        OpenAiEmbeddingResponse response = new OpenAiEmbeddingResponse(List.of());

        when(client.getEmbeddings(anyString(), any(OpenAiEmbeddingRequest.class)))
                .thenReturn(response);

        assertThatThrownBy(() -> service.generateEmbedding("test text"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Empty or null response");
    }

    @Test
    void generateEmbedding_shouldThrowExceptionOnClientError() {
        when(client.getEmbeddings(anyString(), any(OpenAiEmbeddingRequest.class)))
                .thenThrow(new RuntimeException("API error"));

        assertThatThrownBy(() -> service.generateEmbedding("test text"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Embedding generation failed");
    }

    // Helper to fix any(...) matchers
    private static <T> T any(Class<T> type) {
        return org.mockito.ArgumentMatchers.any(type);
    }
}
