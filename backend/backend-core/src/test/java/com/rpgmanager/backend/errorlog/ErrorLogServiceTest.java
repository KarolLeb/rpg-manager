package com.rpgmanager.backend.errorlog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErrorLogServiceTest {

  @Mock private ErrorLogRepository errorLogRepository;

  @InjectMocks private ErrorLogService errorLogService;

  private CreateErrorLogRequest request;
  private ErrorLogEntry savedEntry;

  @BeforeEach
  void setUp() {
    request = new CreateErrorLogRequest();
    request.setServiceName("backend-core");
    request.setSeverity(ErrorLogEntry.Severity.ERROR);
    request.setMessage("NullPointerException in SessionService");
    request.setStackTrace("java.lang.NullPointerException\n\tat ...");
    request.setCorrelationId("abc-123");
    request.setRequestPath("/api/sessions/1");

    savedEntry =
        ErrorLogEntry.builder()
            .id(100L)
            .serviceName("backend-core")
            .severity(ErrorLogEntry.Severity.ERROR)
            .message("NullPointerException in SessionService")
            .stackTrace("java.lang.NullPointerException\n\tat ...")
            .correlationId("abc-123")
            .requestPath("/api/sessions/1")
            .createdAt(OffsetDateTime.now())
            .build();
  }

  @Test
  void logError_shouldSaveAndReturnDto() {
    when(errorLogRepository.save(any(ErrorLogEntry.class))).thenReturn(savedEntry);

    ErrorLogDto result = errorLogService.logError(request);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(100L);
    assertThat(result.getServiceName()).isEqualTo("backend-core");
    assertThat(result.getSeverity()).isEqualTo(ErrorLogEntry.Severity.ERROR);
    assertThat(result.getMessage()).isEqualTo("NullPointerException in SessionService");
    assertThat(result.getCorrelationId()).isEqualTo("abc-123");
    verify(errorLogRepository).save(any(ErrorLogEntry.class));
  }

  @Test
  void getError_shouldReturnDto() {
    when(errorLogRepository.findById(100L)).thenReturn(Optional.of(savedEntry));

    ErrorLogDto result = errorLogService.getError(100L);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(100L);
  }

  @Test
  void getError_shouldThrowIfNotFound() {
    when(errorLogRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> errorLogService.getError(999L))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Error log entry not found with id: 999");
  }

  @Test
  void getErrors_shouldFilterBySeverity() {
    when(errorLogRepository.findBySeverityOrderByCreatedAtDesc(ErrorLogEntry.Severity.ERROR))
        .thenReturn(List.of(savedEntry));

    List<ErrorLogDto> results =
        errorLogService.getErrors(ErrorLogEntry.Severity.ERROR, null, null, null);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getSeverity()).isEqualTo(ErrorLogEntry.Severity.ERROR);
  }

  @Test
  void getErrors_shouldFilterByServiceName() {
    when(errorLogRepository.findByServiceNameOrderByCreatedAtDesc("backend-core"))
        .thenReturn(List.of(savedEntry));

    List<ErrorLogDto> results = errorLogService.getErrors(null, "backend-core", null, null);

    assertThat(results).hasSize(1);
    assertThat(results.get(0).getServiceName()).isEqualTo("backend-core");
  }

  @Test
  void getErrors_shouldFilterBySeverityAndServiceName() {
    when(errorLogRepository.findBySeverityAndServiceNameOrderByCreatedAtDesc(
            ErrorLogEntry.Severity.ERROR, "backend-core"))
        .thenReturn(List.of(savedEntry));

    List<ErrorLogDto> results =
        errorLogService.getErrors(ErrorLogEntry.Severity.ERROR, "backend-core", null, null);

    assertThat(results).hasSize(1);
  }

  @Test
  void getErrors_shouldFilterByDateRange() {
    OffsetDateTime from = OffsetDateTime.now().minusDays(1);
    OffsetDateTime to = OffsetDateTime.now();
    when(errorLogRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(from, to))
        .thenReturn(List.of(savedEntry));

    List<ErrorLogDto> results = errorLogService.getErrors(null, null, from, to);

    assertThat(results).hasSize(1);
  }

  @Test
  void getErrors_shouldReturnAllWhenNoFilters() {
    when(errorLogRepository.findAll()).thenReturn(List.of(savedEntry));

    List<ErrorLogDto> results = errorLogService.getErrors(null, null, null, null);

    assertThat(results).hasSize(1);
  }

  @Test
  void getErrors_shouldReturnEmptyListWhenNoMatches() {
    when(errorLogRepository.findBySeverityOrderByCreatedAtDesc(ErrorLogEntry.Severity.CRITICAL))
        .thenReturn(Collections.emptyList());

    List<ErrorLogDto> results =
        errorLogService.getErrors(ErrorLogEntry.Severity.CRITICAL, null, null, null);

    assertThat(results).isEmpty();
  }
}
