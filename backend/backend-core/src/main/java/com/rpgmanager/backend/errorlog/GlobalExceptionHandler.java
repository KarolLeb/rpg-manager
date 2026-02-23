package com.rpgmanager.backend.errorlog;

import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler that catches unhandled exceptions, logs them to the error log system,
 * and returns structured JSON error responses.
 */
@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

  private static final String SERVICE_NAME = "rpgmanager-backend";
  private final ErrorLogService errorLogService;

  /**
   * Handles IllegalArgumentException (validation / not-found errors).
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return 400 Bad Request with error details
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    String correlationId = generateCorrelationId();
    log.warn("Bad request [{}]: {}", correlationId, ex.getMessage());

    logError(ErrorLogEntry.Severity.WARN, ex, request, correlationId);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), correlationId));
  }

  /**
   * Handles RuntimeException (application errors).
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return 500 Internal Server Error with error details
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleRuntimeException(
      RuntimeException ex, HttpServletRequest request) {
    String correlationId = generateCorrelationId();
    log.error("Runtime error [{}]: {}", correlationId, ex.getMessage(), ex);

    logError(ErrorLogEntry.Severity.ERROR, ex, request, correlationId);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                correlationId));
  }

  /**
   * Handles all other unexpected exceptions.
   *
   * @param ex the exception
   * @param request the HTTP request
   * @return 500 Internal Server Error with error details
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex, HttpServletRequest request) {
    String correlationId = generateCorrelationId();
    log.error("Unexpected error [{}]: {}", correlationId, ex.getMessage(), ex);

    logError(ErrorLogEntry.Severity.CRITICAL, ex, request, correlationId);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                correlationId));
  }

  private void logError(
      ErrorLogEntry.Severity severity,
      Exception ex,
      HttpServletRequest request,
      String correlationId) {
    try {
      CreateErrorLogRequest errorLog = new CreateErrorLogRequest();
      errorLog.setServiceName(SERVICE_NAME);
      errorLog.setSeverity(severity);
      errorLog.setMessage(ex.getMessage());
      errorLog.setStackTrace(getStackTrace(ex));
      errorLog.setCorrelationId(correlationId);
      errorLog.setRequestPath(request.getMethod() + " " + request.getRequestURI());

      errorLogService.logError(errorLog);
    } catch (Exception logEx) {
      log.error(
          "Failed to persist error log for correlation {}: {}", correlationId, logEx.getMessage());
    }
  }

  private String getStackTrace(Exception ex) {
    StringWriter sw = new StringWriter();
    ex.printStackTrace(new PrintWriter(sw));
    String trace = sw.toString();
    // Limit stack trace size to avoid excessively large DB entries
    return trace.length() > 4000 ? trace.substring(0, 4000) : trace;
  }

  private String generateCorrelationId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
  }

  /** Structured error response returned to API clients. */
  public record ErrorResponse(int status, String error, String message, String correlationId) {}
}
