package com.rpgmanager.backend.errorlog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

  @Mock private ErrorLogService errorLogService;
  @Mock private HttpServletRequest request;

  @InjectMocks private GlobalExceptionHandler handler;

  @Test
  void handleIllegalArgument_shouldReturn400AndLogAsWarn() {
    setupRequest("GET", "/api/characters/999");
    IllegalArgumentException ex = new IllegalArgumentException("Character not found with id: 999");

    ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
        handler.handleIllegalArgument(ex, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(400);
    assertThat(response.getBody().message()).isEqualTo("Character not found with id: 999");
    assertThat(response.getBody().correlationId()).isNotBlank();

    ArgumentCaptor<CreateErrorLogRequest> captor =
        ArgumentCaptor.forClass(CreateErrorLogRequest.class);
    verify(errorLogService).logError(captor.capture());
    assertThat(captor.getValue().getSeverity()).isEqualTo(ErrorLogEntry.Severity.WARN);
    assertThat(captor.getValue().getRequestPath()).isEqualTo("GET /api/characters/999");
  }

  @Test
  void handleRuntimeException_shouldReturn500AndLogAsError() {
    setupRequest("POST", "/api/sessions");
    RuntimeException ex = new RuntimeException("Database connection failed");

    ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
        handler.handleRuntimeException(ex, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(500);
    assertThat(response.getBody().message()).isEqualTo("Database connection failed");

    ArgumentCaptor<CreateErrorLogRequest> captor =
        ArgumentCaptor.forClass(CreateErrorLogRequest.class);
    verify(errorLogService).logError(captor.capture());
    assertThat(captor.getValue().getSeverity()).isEqualTo(ErrorLogEntry.Severity.ERROR);
    assertThat(captor.getValue().getStackTrace()).isNotBlank();
  }

  @Test
  void handleException_shouldReturn500AndLogAsCritical() {
    setupRequest("PUT", "/api/campaigns/1");
    Exception ex = new Exception("Unexpected failure");

    ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
        handler.handleException(ex, request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred");

    ArgumentCaptor<CreateErrorLogRequest> captor =
        ArgumentCaptor.forClass(CreateErrorLogRequest.class);
    verify(errorLogService).logError(captor.capture());
    assertThat(captor.getValue().getSeverity()).isEqualTo(ErrorLogEntry.Severity.CRITICAL);
    assertThat(captor.getValue().getServiceName()).isEqualTo("rpgmanager-backend");
  }

  @Test
  void handleException_shouldIncludeCorrelationId() {
    setupRequest("GET", "/api/test");
    RuntimeException ex = new RuntimeException("test");

    ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
        handler.handleRuntimeException(ex, request);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().correlationId()).hasSize(16);
  }

  @Test
  void handleException_shouldStillReturnResponseWhenLoggingFails() {
    setupRequest("GET", "/api/test");
    org.mockito.Mockito.doThrow(new RuntimeException("DB down"))
        .when(errorLogService)
        .logError(any());

    IllegalArgumentException ex = new IllegalArgumentException("Not found");

    ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
        handler.handleIllegalArgument(ex, request);

    // Should still return a proper response even though logging failed
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().message()).isEqualTo("Not found");
  }

  @Test
  void handleException_shouldTruncateLongStackTrace() {
    setupRequest("GET", "/api/test");
    // Create an exception with many nested causes to increase stack trace length
    Exception ex = new Exception("Root");
    for (int i = 0; i < 100; i++) {
      ex = new Exception("Layer " + i, ex);
    }

    handler.handleException(ex, request);

    ArgumentCaptor<CreateErrorLogRequest> captor =
        ArgumentCaptor.forClass(CreateErrorLogRequest.class);
    verify(errorLogService).logError(captor.capture());
    assertThat(captor.getValue().getStackTrace()).hasSizeLessThanOrEqualTo(4000);
  }

  private void setupRequest(String method, String uri) {
    org.mockito.Mockito.lenient().when(request.getMethod()).thenReturn(method);
    org.mockito.Mockito.lenient().when(request.getRequestURI()).thenReturn(uri);
  }
}
