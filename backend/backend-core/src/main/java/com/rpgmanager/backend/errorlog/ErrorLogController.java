package com.rpgmanager.backend.errorlog;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for error log operations — logging and querying application
 * errors.
 */
@RestController
@RequestMapping("/api/error-log")
@RequiredArgsConstructor
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    /**
     * Logs a new application error.
     *
     * @param request the error details
     * @return the created error log entry
     */
    @PostMapping
    public ErrorLogDto logError(@RequestBody CreateErrorLogRequest request) {
        return errorLogService.logError(request);
    }

    /**
     * Retrieves error log entries with optional filters.
     *
     * @param severity optional severity filter (WARN, ERROR, CRITICAL)
     * @param service  optional service name filter
     * @param from     optional start date filter (ISO format)
     * @param to       optional end date filter (ISO format)
     * @return filtered list of error log entries
     */
    @GetMapping
    public List<ErrorLogDto> getErrors(
            @RequestParam(required = false) ErrorLogEntry.Severity severity,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to) {
        return errorLogService.getErrors(severity, service, from, to);
    }

    /**
     * Retrieves a single error log entry by ID.
     *
     * @param id the error log entry ID
     * @return the error log entry details
     */
    @GetMapping("/{id}")
    public ErrorLogDto getError(@PathVariable Long id) {
        return errorLogService.getError(id);
    }
}
