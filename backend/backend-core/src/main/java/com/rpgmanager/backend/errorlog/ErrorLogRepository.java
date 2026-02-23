package com.rpgmanager.backend.errorlog;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for error log entries with filtering capabilities. */
@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLogEntry, Long> {

  List<ErrorLogEntry> findBySeverityOrderByCreatedAtDesc(ErrorLogEntry.Severity severity);

  List<ErrorLogEntry> findByServiceNameOrderByCreatedAtDesc(String serviceName);

  List<ErrorLogEntry> findByCreatedAtBetweenOrderByCreatedAtDesc(
      OffsetDateTime from, OffsetDateTime to);

  List<ErrorLogEntry> findBySeverityAndServiceNameOrderByCreatedAtDesc(
      ErrorLogEntry.Severity severity, String serviceName);
}
