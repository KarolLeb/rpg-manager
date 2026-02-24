package com.rpgmanager.backend.activitylog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ActivityLogServiceTest {

  @Mock private ActivityLogRepository activityLogRepository;
  @Mock private EmbeddingService embeddingService;
  @Mock private ObjectMapper objectMapper;

  @InjectMocks private ActivityLogService activityLogService;

  private CreateActivityLogRequest createRequest;
  private ActivityLogEntry entry;

  @BeforeEach
  void setUp() {
    createRequest = new CreateActivityLogRequest();
    createRequest.setSessionId(1L);
    createRequest.setCampaignId(2L);
    createRequest.setUserId(3L);
    createRequest.setActionType(ActivityLogEntry.ActionType.DICE_ROLL);
    createRequest.setDescription("Rolled a 20");
    createRequest.setMetadata(Map.of("die", "d20", "result", 20));

    entry =
        ActivityLogEntry.builder()
            .id(100L)
            .sessionId(1L)
            .campaignId(2L)
            .userId(3L)
            .actionType(ActivityLogEntry.ActionType.DICE_ROLL)
            .description("Rolled a 20")
            .metadata("{\"die\":\"d20\",\"result\":20}")
            .createdAt(OffsetDateTime.now())
            .build();
  }

  @Test
  void shouldLogActivity() throws JsonProcessingException {
    // given
    float[] embedding = new float[384];
    embedding[0] = 1.0f;
    when(embeddingService.embed(any())).thenReturn(embedding);
    when(objectMapper.writeValueAsString(any())).thenReturn("{\"die\":\"d20\",\"result\":20}");
    when(activityLogRepository.save(any(ActivityLogEntry.class))).thenReturn(entry);
    when(objectMapper.readValue(anyString(), any(TypeReference.class)))
        .thenReturn(createRequest.getMetadata());

    // when
    ActivityLogDto result = activityLogService.logActivity(createRequest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(100L);
    assertThat(result.getDescription()).isEqualTo("Rolled a 20");
    verify(activityLogRepository).save(any(ActivityLogEntry.class));
  }

  @Test
  void shouldSearchActivities() throws JsonProcessingException {
    // given
    float[] queryEmbedding = new float[384];
    queryEmbedding[0] = 0.5f;
    when(embeddingService.embed("query")).thenReturn(queryEmbedding);

    Object[] row = new Object[10];
    row[0] = 100L; // id
    row[1] = 1L; // sessionId
    row[2] = 2L; // campaignId
    row[3] = 3L; // userId
    row[4] = "DICE_ROLL"; // actionType
    row[5] = "Rolled a 20"; // description
    row[6] = "{\"die\":\"d20\"}"; // metadata
    row[7] = null; // embedding
    row[8] = Timestamp.from(Instant.now()); // createdAt
    row[9] = new BigDecimal("0.95"); // similarityScore

    List<Object[]> resultsList = Collections.singletonList(row);
    when(activityLogRepository.findSimilar(any(), eq(5))).thenReturn(resultsList);
    when(objectMapper.readValue(anyString(), any(TypeReference.class)))
        .thenReturn(Map.of("die", "d20"));

    // when
    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(100L);
    assertThat(results.get(0).getSimilarityScore()).isEqualTo(0.95);
  }

  @Test
  void shouldGetActivitiesBySession() {
    // given
    when(activityLogRepository.findBySessionIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(entry));

    // when
    List<ActivityLogDto> results = activityLogService.getActivitiesBySession(1L);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(100L);
  }

  @Test
  void shouldGetActivitiesByCampaign() {
    // given
    when(activityLogRepository.findByCampaignIdOrderByCreatedAtDesc(2L)).thenReturn(List.of(entry));

    // when
    List<ActivityLogDto> results = activityLogService.getActivitiesByCampaign(2L);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getId()).isEqualTo(100L);
  }

  @Test
  void shouldHandleNullMetadataAndEmbeddings() {
    // given
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = null; // sessionId
    row[2] = null; // campaignId
    row[3] = null; // userId
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = null; // metadata
    row[7] = null;
    row[8] = OffsetDateTime.now(); // createdAt as OffsetDateTime
    row[9] = null; // similarityScore

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));

    // when
    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getSessionId()).isNull();
    assertThat(results.get(0).getMetadata()).isEmpty();
    assertThat(results.get(0).getSimilarityScore()).isNull();
  }

  @Test
  void shouldHandleEmptyRequestMetadata() {
    // given
    createRequest.setMetadata(Collections.emptyMap());
    when(embeddingService.embed(any())).thenReturn(new float[384]);
    when(activityLogRepository.save(any())).thenReturn(entry);

    // when
    activityLogService.logActivity(createRequest);

    // then
    verify(activityLogRepository).save(any());
  }

  @Test
  void shouldHandleBlankMetadata() {
    // given
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = 1L;
    row[2] = 2L;
    row[3] = 3L;
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = "  "; // blank metadata
    row[7] = null;
    row[8] = OffsetDateTime.now();
    row[9] = null;

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));

    // when
    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getMetadata()).isEmpty();
  }

  @Test
  void shouldHandleJsonProcessingExceptionOnSerialize() throws JsonProcessingException {
    // given
    createRequest.setMetadata(Map.of("key", "value"));
    when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("error") {});

    // when / then
    assertThatThrownBy(() -> activityLogService.logActivity(createRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Failed to serialize metadata");
  }

  @Test
  void shouldHandleJsonProcessingExceptionOnDeserialize() throws JsonProcessingException {
    // given
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = 1L;
    row[2] = 2L;
    row[3] = 3L;
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = "invalid json";
    row[7] = null;
    row[8] = OffsetDateTime.now();
    row[9] = null;

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));
    when(objectMapper.readValue(anyString(), any(TypeReference.class)))
        .thenThrow(new JsonProcessingException("error") {});

    // when
    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getMetadata()).isEmpty();
  }

  @Test
  void shouldHandleNullVectorInVectorToString() {
    // given
    when(embeddingService.embed("query")).thenReturn(null);
    when(activityLogRepository.findSimilar("[]", 5)).thenReturn(Collections.emptyList());

    // when
    activityLogService.searchActivities("query", 5);

    // then
    verify(activityLogRepository).findSimilar("[]", 5);
  }

  @Test
  void shouldHandleSingleElementVectorInVectorToString() {
    // given
    float[] vector = new float[] {1.0f};
    when(embeddingService.embed("query")).thenReturn(vector);
    when(activityLogRepository.findSimilar("[1.0]", 5)).thenReturn(Collections.emptyList());

    // when
    activityLogService.searchActivities("query", 5);

    // then
    verify(activityLogRepository).findSimilar("[1.0]", 5);
  }

  @Test
  void toOffsetDateTime_shouldHandleTimestamp() {
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = 1L;
    row[2] = 2L;
    row[3] = 3L;
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = "{}";
    row[7] = null;
    row[8] = Timestamp.from(Instant.now());
    row[9] = null;

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));

    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    assertThat(results.get(0).getCreatedAt()).isNotNull();
  }

  @Test
  void toOffsetDateTime_shouldHandleOffsetDateTime() {
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = 1L;
    row[2] = 2L;
    row[3] = 3L;
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = "{}";
    row[7] = null;
    row[8] = OffsetDateTime.now();
    row[9] = null;

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));

    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    assertThat(results.get(0).getCreatedAt()).isNotNull();
  }

  @Test
  void deserializeMetadata_shouldHandleEmptyJson() {
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = 1L;
    row[2] = 2L;
    row[3] = 3L;
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = "";
    row[7] = null;
    row[8] = OffsetDateTime.now();
    row[9] = null;

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));

    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    assertThat(results.get(0).getMetadata()).isEmpty();
  }

  @Test
  void deserializeMetadata_shouldHandleBlankJson() {
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = 1L;
    row[2] = 2L;
    row[3] = 3L;
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = "   ";
    row[7] = null;
    row[8] = OffsetDateTime.now();
    row[9] = null;

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));

    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    assertThat(results.get(0).getMetadata()).isEmpty();
  }

  @Test
  void shouldHandleOtherTypesInToOffsetDateTime() {
    // given
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = 1L;
    row[2] = 2L;
    row[3] = 3L;
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = "{}";
    row[7] = null;
    row[8] = "invalid date type"; // some other type
    row[9] = null;

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));

    // when
    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    // then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getCreatedAt()).isNull();
  }

  @Test
  void toOffsetDateTime_shouldHandleNull() {
    Object[] row = new Object[10];
    row[0] = 100L;
    row[1] = 1L;
    row[2] = 2L;
    row[3] = 3L;
    row[4] = "DICE_ROLL";
    row[5] = "Description";
    row[6] = "{}";
    row[7] = null;
    row[8] = null;
    row[9] = null;

    when(activityLogRepository.findSimilar(any(), eq(5)))
        .thenReturn(Collections.singletonList(row));

    List<ActivityLogDto> results = activityLogService.searchActivities("query", 5);

    assertThat(results.get(0).getCreatedAt()).isNull();
  }

  @Test
  void serializeMetadata_shouldHandleNull() {
    CreateActivityLogRequest req = new CreateActivityLogRequest();
    req.setMetadata(null);
    when(embeddingService.embed(any())).thenReturn(new float[384]);
    when(activityLogRepository.save(any())).thenReturn(entry);

    activityLogService.logActivity(req);

    verify(activityLogRepository).save(argThat(e -> e.getMetadata() == null));
  }

  @Test
  void serializeMetadata_shouldHandleEmpty() {
    CreateActivityLogRequest req = new CreateActivityLogRequest();
    req.setMetadata(Collections.emptyMap());
    when(embeddingService.embed(any())).thenReturn(new float[384]);
    when(activityLogRepository.save(any())).thenReturn(entry);

    activityLogService.logActivity(req);

    verify(activityLogRepository).save(argThat(e -> e.getMetadata() == null));
  }
}
