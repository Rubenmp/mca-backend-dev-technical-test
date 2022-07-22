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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mca.yourapp.service.impl.LogServiceImpl.LOGS_ENABLED;
import static com.mca.yourapp.service.impl.LogServiceImpl.LOG_SEP;
import static com.mca.yourapp.service.impl.LogServiceImpl.START_LOG_BAD_REQUEST;
import static com.mca.yourapp.service.impl.LogServiceImpl.START_LOG_REG;
import static com.mca.yourapp.service.impl.LogServiceImpl.START_LOG_REQUEST;
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
    void log_analysis() {
        Assumptions.assumeTrue(LOGS_ENABLED, "Logs must be enabled");

        final List<Log> allLogs = logService.getLogs();
        final List<Log> infoLogs = allLogs.stream().filter(t -> t.getType().equals(LogType.INFO)).toList();
        final List<Log> warningLogs = allLogs.stream().filter(t -> t.getType().equals(LogType.WARNING)).toList();
        final List<Log> errorLogs = allLogs.stream().filter(t -> t.getType().equals(LogType.ERROR)).toList();

        Map<Long, Long> requestToProductMap = getRequestToProductMap(infoLogs);

        // 14557
        Set<Long> allRequestIds = requestToProductMap.keySet();

        // 3970
        Set<Long> requestIdsNotFound = getRequestIdsNotFound(infoLogs);

        // 10633
        Set<Log> successfulRequestResponses = infoLogs.stream().filter(l -> l.getMessage().contains(">")).collect(Collectors.toSet());

        /*
         * Total requests: 14557
         *
         * - Not found responses: 3970
         * - Successful response: 10633
         *
         * Structure of successful responses
         *  "1" -> "[2, 3, 4]"
         *  "2" -> "[3, 100, 1000]"
         *  "3" -> "[100, 1000]"
         *  "4" -> "[1, 2]"
         *
         * */
        Map<String, String> requestResponsesMap = successfulRequestResponses.stream().map(t -> t.getMessage().split(">"))
                .filter(l -> isLong(l[1]))
                .collect(Collectors.toMap(t -> t[2], t -> t[3], (t1, t2) -> t1));
        Set<Long> successfulRequestIds = successfulRequestResponses.stream().map(t -> t.getMessage().split(">")[1]).filter(this::isLong).map(this::parseLong).collect(Collectors.toSet());


        // Filter requests without known response
        // allRequestIds - successfulRequestIds - requestIdsNotFound
        Set<Long> weirdRequests = allRequestIds.stream().filter(id -> !successfulRequestIds.contains(id)).filter(id -> !requestIdsNotFound.contains(id)).collect(Collectors.toSet());

        // All the weird requests are for input productIds {"3", "5"} which are known use cases with valid responses
        // {String[3]@4213} ["", "32229", "3"]
        // {String[3]@4207} ["", "32183", "3"]
        // {String[3]@4208} ["", "26334", "5"]
        // {String[3]@4209} ["", "32181", "3"]
        // {String[3]@4210} ["", "32198", "3"]
        // {String[3]@4211} ["", "32258", "3"]
        // {String[3]@4212} ["", "32196", "3"]
    }

    private Set<Long> getRequestIdsNotFound(final List<Log> infoLogs) {
        final String requestLogStart = START_LOG_REG + START_LOG_BAD_REQUEST;

        return infoLogs.stream().filter(Objects::nonNull).map(Log::getMessage).filter(Objects::nonNull).filter(message -> message.startsWith(requestLogStart))
                .map(t -> t.split("\\*")).filter(t -> t.length == 2 && isLong(t[1])).map(t -> parseLong(t[1])).collect(Collectors.toSet());
    }

    private Map<Long, Long> getRequestToProductMap(final List<Log> infoLogs) {
        final String requestLogStart = START_LOG_REG + START_LOG_REQUEST;

        return infoLogs.stream().filter(Objects::nonNull).map(Log::getMessage).filter(Objects::nonNull).filter(message -> message.startsWith(requestLogStart))
                .map(message -> message.substring(requestLogStart.length())).map(m -> m.split(LOG_SEP)).filter(t -> t.length == 2 && isLong(t[0]) && isLong(t[1]))
                .collect(Collectors.toMap(t -> parseLong(t[0]), t -> parseLong(t[1])));
    }


    private Long parseLong(final String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    private boolean isLong(final String str) {
        return parseLong(str) != null;
    }



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
