package com.rpgmanager.backend.errorlog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ErrorLogControllerTest {

    @Mock
    private ErrorLogService errorLogService;

    @InjectMocks
    private ErrorLogController controller;

    @Test
    void logError() {
        CreateErrorLogRequest request = new CreateErrorLogRequest();
        ErrorLogDto dto = new ErrorLogDto(1L, "service", ErrorLogEntry.Severity.CRITICAL, "msg", "trc", "cor", 2L,
                "path", null);
        when(errorLogService.logError(request)).thenReturn(dto);

        ErrorLogDto result = controller.logError(request);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getErrors() {
        ErrorLogDto dto = new ErrorLogDto(1L, "service", ErrorLogEntry.Severity.CRITICAL, "msg", "trc", "cor", 2L,
                "path", null);
        when(errorLogService.getErrors(null, null, null, null)).thenReturn(Collections.singletonList(dto));

        List<ErrorLogDto> result = controller.getErrors(null, null, null, null);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
    }

    @Test
    void getError() {
        ErrorLogDto dto = new ErrorLogDto(1L, "service", ErrorLogEntry.Severity.CRITICAL, "msg", "trc", "cor", 2L,
                "path", null);
        when(errorLogService.getError(1L)).thenReturn(dto);

        ErrorLogDto result = controller.getError(1L);
        assertThat(result).isEqualTo(dto);
    }
}
