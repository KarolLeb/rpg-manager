package com.rpgmanager.backend.errorlog;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for error log responses. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorLogDto {

    private Long id;
    private String serviceName;
    private ErrorLogEntry.Severity severity;
    private String message;
    private String stackTrace;
    private String correlationId;
    private Long userId;
    private String requestPath;
    private OffsetDateTime createdAt;
}
