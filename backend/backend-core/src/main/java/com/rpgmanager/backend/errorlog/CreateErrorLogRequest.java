package com.rpgmanager.backend.errorlog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Request DTO for creating a new error log entry. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateErrorLogRequest {

  private String serviceName;
  private ErrorLogEntry.Severity severity;
  private String message;
  private String stackTrace;
  private String correlationId;
  private Long userId;
  private String requestPath;
}
