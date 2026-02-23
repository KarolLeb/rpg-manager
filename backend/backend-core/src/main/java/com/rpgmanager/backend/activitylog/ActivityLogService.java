package com.rpgmanager.backend.activitylog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for managing activity log entries with embedding-based semantic search. */
@Service
@RequiredArgsConstructor
public class ActivityLogService {

  private final ActivityLogRepository activityLogRepository;
  private final EmbeddingService embeddingService;
  private final ObjectMapper objectMapper;

  /**
   * Logs a new activity, generating an embedding for semantic search.
   *
   * @param request the activity details
   * @return the created activity log DTO
   */
  @Transactional
  public ActivityLogDto logActivity(CreateActivityLogRequest request) {
    String metadataJson = serializeMetadata(request.getMetadata());
    float[] embedding = embeddingService.embed(request.getDescription());

    ActivityLogEntry entry =
        ActivityLogEntry.builder()
            .sessionId(request.getSessionId())
            .campaignId(request.getCampaignId())
            .userId(request.getUserId())
            .actionType(request.getActionType())
            .description(request.getDescription())
            .metadata(metadataJson)
            .embedding(embedding)
            .build();

    ActivityLogEntry saved = activityLogRepository.save(entry);
    return toDto(saved, null);
  }

  /**
   * Performs a semantic search over activity logs using vector similarity.
   *
   * @param query the natural-language search query
   * @param limit maximum number of results
   * @return activities ordered by semantic similarity
   */
  @Transactional(readOnly = true)
  public List<ActivityLogDto> searchActivities(String query, int limit) {
    float[] queryEmbedding = embeddingService.embed(query);
    String embeddingStr = vectorToString(queryEmbedding);

    List<Object[]> results = activityLogRepository.findSimilar(embeddingStr, limit);
    return results.stream().map(this::mapNativeResult).collect(Collectors.toList());
  }

  /**
   * Retrieves activities for a specific session.
   *
   * @param sessionId the session ID
   * @return list of activity log DTOs ordered by creation time (newest first)
   */
  @Transactional(readOnly = true)
  public List<ActivityLogDto> getActivitiesBySession(Long sessionId) {
    return activityLogRepository.findBySessionIdOrderByCreatedAtDesc(sessionId).stream()
        .map(entry -> toDto(entry, null))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves activities for a specific campaign.
   *
   * @param campaignId the campaign ID
   * @return list of activity log DTOs ordered by creation time (newest first)
   */
  @Transactional(readOnly = true)
  public List<ActivityLogDto> getActivitiesByCampaign(Long campaignId) {
    return activityLogRepository.findByCampaignIdOrderByCreatedAtDesc(campaignId).stream()
        .map(entry -> toDto(entry, null))
        .collect(Collectors.toList());
  }

  private ActivityLogDto toDto(ActivityLogEntry entry, Double similarityScore) {
    return ActivityLogDto.builder()
        .id(entry.getId())
        .sessionId(entry.getSessionId())
        .campaignId(entry.getCampaignId())
        .userId(entry.getUserId())
        .actionType(entry.getActionType())
        .description(entry.getDescription())
        .metadata(deserializeMetadata(entry.getMetadata()))
        .createdAt(entry.getCreatedAt())
        .similarityScore(similarityScore)
        .build();
  }

  private ActivityLogDto mapNativeResult(Object[] row) {
    return ActivityLogDto.builder()
        .id(((Number) row[0]).longValue())
        .sessionId(row[1] != null ? ((Number) row[1]).longValue() : null)
        .campaignId(row[2] != null ? ((Number) row[2]).longValue() : null)
        .userId(row[3] != null ? ((Number) row[3]).longValue() : null)
        .actionType(ActivityLogEntry.ActionType.valueOf((String) row[4]))
        .description((String) row[5])
        .metadata(deserializeMetadata((String) row[6]))
        .createdAt(toOffsetDateTime(row[8]))
        .similarityScore(row[9] != null ? ((BigDecimal) row[9]).doubleValue() : null)
        .build();
  }

  private OffsetDateTime toOffsetDateTime(Object value) {
    if (value instanceof Timestamp ts) {
      return ts.toInstant().atOffset(ZoneOffset.UTC);
    }
    if (value instanceof OffsetDateTime odt) {
      return odt;
    }
    return null;
  }

  private String serializeMetadata(Map<String, Object> metadata) {
    if (metadata == null || metadata.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(metadata);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Failed to serialize metadata", e);
    }
  }

  private Map<String, Object> deserializeMetadata(String json) {
    if (json == null || json.isBlank()) {
      return Collections.emptyMap();
    }
    try {
      return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    } catch (JsonProcessingException e) {
      return Collections.emptyMap();
    }
  }

  private String vectorToString(float[] vector) {
    if (vector == null) {
      return "[]";
    }
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < vector.length; i++) {
      sb.append(vector[i]);
      if (i < vector.length - 1) {
        sb.append(",");
      }
    }
    sb.append("]");
    return sb.toString();
  }
}
