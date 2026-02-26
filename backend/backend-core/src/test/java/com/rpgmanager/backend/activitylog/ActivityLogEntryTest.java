package com.rpgmanager.backend.activitylog;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class ActivityLogEntryTest {

  @Test
  void onCreate_setsCreatedAtIfNull() {
    ActivityLogEntry entry = new ActivityLogEntry();
    entry.onCreate();
    assertThat(entry.getCreatedAt()).isNotNull();
  }

  @Test
  void onCreate_doesNotOverrideCreatedAtIfNotNull() {
    ActivityLogEntry entry = new ActivityLogEntry();
    OffsetDateTime time = OffsetDateTime.now().minusDays(1);
    entry.setCreatedAt(time);
    entry.onCreate();
    assertThat(entry.getCreatedAt()).isEqualTo(time);
  }
}
