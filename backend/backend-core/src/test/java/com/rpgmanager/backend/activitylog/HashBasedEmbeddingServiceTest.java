package com.rpgmanager.backend.activitylog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HashBasedEmbeddingServiceTest {

  private final HashBasedEmbeddingService embeddingService = new HashBasedEmbeddingService();

  @Test
  void shouldGenerateEmbeddingForText() {
    // when
    float[] result = embeddingService.embed("Testing some text");

    // then
    assertThat(result).hasSize(384);
    // Check that it's normalized (sum of squares is approx 1)
    float sumOfSquares = 0;
    for (float v : result) {
      sumOfSquares += v * v;
    }
    assertThat(sumOfSquares).isBetween(0.99f, 1.01f);
  }

  @Test
  void shouldReturnEmptyVectorForNullOrBlank() {
    float[] resultNull = embeddingService.embed(null);
    assertThat(resultNull).hasSize(384);
    for (float v : resultNull) {
      assertThat(v).isZero();
    }

    float[] resultBlank = embeddingService.embed("   ");
    assertThat(resultBlank).hasSize(384);
    for (float v : resultBlank) {
      assertThat(v).isZero();
    }
  }

  @Test
  void shouldBeDeterministic() {
    float[] res1 = embeddingService.embed("Test text");
    float[] res2 = embeddingService.embed("Test text");
    assertThat(res1).isEqualTo(res2);
  }

  @Test
  void shouldBeCaseInsensitive() {
    float[] res1 = embeddingService.embed("Test text");
    float[] res2 = embeddingService.embed("TEST TEXT");
    assertThat(res1).isEqualTo(res2);
  }

  @Test
  void shouldEmbedSingleTokenCorrectly() {
    float[] result = embeddingService.embed("token");
    assertThat(result).hasSize(384);
    float norm = 0;
    for (float v : result) {
      norm += v * v;
    }
    assertThat((float) Math.sqrt(norm)).isBetween(0.99f, 1.01f);
  }

  @Test
  void shouldHandleMultipleTokensCorrectly() {
    float[] result = embeddingService.embed("multiple tokens in one string");
    assertThat(result).hasSize(384);
    float norm = 0;
    for (float v : result) {
      norm += v * v;
    }
    assertThat((float) Math.sqrt(norm)).isBetween(0.99f, 1.01f);
  }

  @Test
  void shouldHandleTokensWithDifferentLengths() {
    float[] res1 = embeddingService.embed("a");
    float[] res2 = embeddingService.embed("verylongtokenthatshouldbehashedproperly");
    assertThat(res1).isNotEqualTo(res2);
  }

  @Test
  void shouldReturnCorrectDimension() {
    assertThat(embeddingService.getDimension()).isEqualTo(384);
  }
}
