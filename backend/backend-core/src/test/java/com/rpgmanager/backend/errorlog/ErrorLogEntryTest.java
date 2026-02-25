package com.rpgmanager.backend.errorlog;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class ErrorLogEntryTest {

    @Test
    void onCreate_setsCreatedAtIfNull() {
        ErrorLogEntry entry = new ErrorLogEntry();
        entry.onCreate();
        assertThat(entry.getCreatedAt()).isNotNull();
    }

    @Test
    void onCreate_doesNotOverrideCreatedAtIfNotNull() {
        ErrorLogEntry entry = new ErrorLogEntry();
        OffsetDateTime time = OffsetDateTime.now().minusDays(1);
        entry.setCreatedAt(time);
        entry.onCreate();
        assertThat(entry.getCreatedAt()).isEqualTo(time);
    }
}
