package com.rpgmanager.backend.errorlog;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for managing error log entries for admin troubleshooting. */
@Service
@RequiredArgsConstructor
public class ErrorLogService {

  private static final String ERROR_NOT_FOUND_MSG = "Error log entry not found with id: ";
  private final ErrorLogRepository errorLogRepository;

  /**
   * Logs an application error.
   *
   * @param request the error details
   * @return the created error log DTO
   */
  @Transactional
  public ErrorLogDto logError(CreateErrorLogRequest request) {
    ErrorLogEntry entry =
        ErrorLogEntry.builder()
            .serviceName(request.getServiceName())
            .severity(request.getSeverity())
            .message(request.getMessage())
            .stackTrace(request.getStackTrace())
            .correlationId(request.getCorrelationId())
            .userId(request.getUserId())
            .requestPath(request.getRequestPath())
            .build();

    ErrorLogEntry saved = errorLogRepository.save(entry);
    return toDto(saved);
  }

  /**
   * Retrieves a single error log entry by ID.
   *
   * @param id the error log entry ID
   * @return the error log DTO
   */
  @Transactional(readOnly = true)
  public ErrorLogDto getError(Long id) {
    ErrorLogEntry entry =
        errorLogRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException(ERROR_NOT_FOUND_MSG + id));
    return toDto(entry);
  }

  /**
   * Retrieves error log entries with optional filters.
   *
   * @param severity optional severity filter
   * @param serviceName optional service name filter
   * @param from optional start date filter
   * @param to optional end date filter
   * @return filtered list of error log DTOs
   */
  @Transactional(readOnly = true)
  public List<ErrorLogDto> getErrors(
      ErrorLogEntry.Severity severity, String serviceName, OffsetDateTime from, OffsetDateTime to) {

    List<ErrorLogEntry> entries;

    if (severity != null && serviceName != null) {
      entries =
          errorLogRepository.findBySeverityAndServiceNameOrderByCreatedAtDesc(
              severity, serviceName);
    } else if (severity != null) {
      entries = errorLogRepository.findBySeverityOrderByCreatedAtDesc(severity);
    } else if (serviceName != null) {
      entries = errorLogRepository.findByServiceNameOrderByCreatedAtDesc(serviceName);
    } else if (from != null && to != null) {
      entries = errorLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to);
    } else {
      entries = errorLogRepository.findAll();
    }

    return entries.stream().map(this::toDto).toList();
  }

  private ErrorLogDto toDto(ErrorLogEntry entry) {
    return ErrorLogDto.builder()
        .id(entry.getId())
        .serviceName(entry.getServiceName())
        .severity(entry.getSeverity())
        .message(entry.getMessage())
        .stackTrace(entry.getStackTrace())
        .correlationId(entry.getCorrelationId())
        .userId(entry.getUserId())
        .requestPath(entry.getRequestPath())
        .createdAt(entry.getCreatedAt())
        .build();
  }
}
