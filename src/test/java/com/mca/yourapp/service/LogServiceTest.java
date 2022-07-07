package com.mca.yourapp.service;

import com.mca.yourapp.service.dto.Log;
import com.mca.yourapp.service.dto.LogType;
import com.mca.yourapp.service.impl.LogServiceImpl;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static com.mca.yourapp.service.impl.LogServiceImpl.LOGS_ENABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

@ExtendWith(MockitoExtension.class)
@Execution(SAME_THREAD) // Warning: these tests can not run in parallel due to same log file operations
class LogServiceTest {
    @InjectMocks
    private LogService logService = new LogServiceImpl();


    @Test
    void log_allLogTypes_success() {
        Assumptions.assumeTrue(LOGS_ENABLED, "Logs must be enabled");

        final String logMessage = "Info log";
        final int numberOfPreviousLogs = logService.getLogs().size();

        logService.log(LogType.INFO, logMessage);
        logService.log(LogType.WARNING, logMessage);
        logService.log(LogType.ERROR, logMessage);

        final List<Log> currentLogs = logService.getLogs();

        assertEquals(numberOfPreviousLogs + 3, currentLogs.size(), "Logs size");

        final int firstLogIndex = currentLogs.size() - 3;
        verifyLog(currentLogs.get(firstLogIndex), LogType.INFO, logMessage);
        verifyLog(currentLogs.get(firstLogIndex + 1), LogType.WARNING, logMessage);
        verifyLog(currentLogs.get(firstLogIndex + 2), LogType.ERROR, logMessage);
    }


    @Test
    void log_exception_success() {
        Assumptions.assumeTrue(LOGS_ENABLED, "Logs must be enabled");
        final int numberOfPreviousLogs = logService.getLogs().size();
        final Exception exception = new NullPointerException("Example exception to log");

        logService.log(exception);

        final List<Log> currentLogs = logService.getLogs();
        assertEquals(numberOfPreviousLogs + 1, currentLogs.size(), "Logs size");
        final Log loggedException = currentLogs.get(currentLogs.size() - 1);
        verifyLog(loggedException, LogType.ERROR, Arrays.toString(exception.getStackTrace()));
    }

    private void verifyLog(final Log log, final LogType expectedType, final String expectedMessage) {
        assertNotNull(log, "Log must not be null");
        assertEquals(expectedType, log.getType(), "Log type");
        assertEquals(expectedMessage, log.getMessage(), "Log message");
        assertNotNull(log.getDate(), "Log date");
        final LocalDateTime now = LocalDateTime.now();
        assertTrue(log.getDate().isBefore(now), "Log date before now");
        assertTrue(log.getDate().isAfter(now.minus(3, ChronoUnit.SECONDS)), "Log date was recent");
    }

}
