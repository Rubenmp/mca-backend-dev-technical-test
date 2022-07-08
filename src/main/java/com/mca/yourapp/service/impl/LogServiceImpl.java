package com.mca.yourapp.service.impl;

import com.mca.yourapp.service.LogService;
import com.mca.yourapp.service.dto.Log;
import com.mca.yourapp.service.dto.LogType;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mca.yourapp.service.utils.PreconditionUtils.requireNotNull;


@Service
@java.lang.SuppressWarnings("java:S106") // Warning in method handleLogError does not make sense
public class LogServiceImpl implements LogService {
    private static final String LOG_FILE_NAME = "yourapp.log";
    private static final String LOG_TYPE_INIT_CHARACTER = "[";
    private static final String LOG_TYPE_END_CHAR = "]";
    private static final String LOG_INIT_MESSAGE_CHAR = ": ";
    public static final boolean LOGS_ENABLED = true;

    @Override
    public void log(final LogType type, final String message) {
        if (!LOGS_ENABLED) {
            return;
        }
        requireNotNull(type, "Log type must be provided.");
        requireNotNull(message, "Log message must be provided.");
        final String logMessage = createLog(type, message);

        writeToLogFile(logMessage);
    }

    /**
     * This method can not be run in parallel (usage of synchronized)
     * because several logs could be mixed.
     * It's better to guarantee that logs are written sequentially.
     * If there is any performance issue it can be fixed by increasing logs granularity
     * in different synchronized methods (using several files)
     * */
    private synchronized void writeToLogFile(final String logMessage) {
        try (FileWriter fw = new FileWriter(LOG_FILE_NAME,true)) {
            fw.write(logMessage);
        } catch (IOException e) {
            handleLogError(e);
        }
    }

    private void handleLogError(final Exception exception) {
        System.err.println("Log exception: " + exception.getMessage());
        // There should be an alternative way to notify/detect this error (i.e. email, external cron job checking logs, ...),
        // not breaking the application.
        // Launching an exception here could lead to an infinite loop when exceptions are logged automatically.
    }


    private String createLog(final LogType type, final String message) {
        final LocalDateTime localDate = LocalDateTime.now();
        final String lineSep = System.lineSeparator();
        return "" + LOG_TYPE_INIT_CHARACTER + type + LOG_TYPE_END_CHAR + " " + localDate + LOG_INIT_MESSAGE_CHAR + lineSep + message + lineSep;
    }

    @Override
    public void log(final Exception exception) {
        final String logMessage = exception.getStackTrace() != null ? Arrays.toString(exception.getStackTrace()) : exception.getMessage();
        log(LogType.ERROR, logMessage);
    }

    @Override
    public List<Log> getLogs() {
        final List<Log> logs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_NAME))) {
            String line = reader.readLine();

            Log currentLog = null;
            String currentLogMessage = "";
            while (line != null) {
                // If the line does not start with the log type it is part of the previous log message.
                final LogType logType = getLogType(line);
                if (logType != null) {
                    final LocalDateTime logDate = getLogDate(line);
                    if (logDate != null) {
                        if (currentLog != null) {
                            currentLog.setMessage(currentLogMessage);
                            logs.add(currentLog);
                            currentLogMessage = "";
                        }
                        currentLog = new Log();
                        currentLog.setType(logType);
                        currentLog.setDate(logDate);
                    }
                } else {
                    currentLogMessage = currentLogMessage.concat(line);
                }

                line = reader.readLine();
            }

            if (currentLog != null) {
                currentLog.setMessage(currentLogMessage);
                logs.add(currentLog);
            }
        } catch (final IOException exception) {
            handleLogError(exception);
        }

        return logs;
    }

    private LocalDateTime getLogDate(final String line) {
        final int endLogTypeIndex = line.indexOf(LOG_TYPE_END_CHAR);
        final int initLogMessageIndex = line.indexOf(LOG_INIT_MESSAGE_CHAR, endLogTypeIndex);
        if (endLogTypeIndex == -1 || initLogMessageIndex == -1 || endLogTypeIndex + 2 >= initLogMessageIndex) {
            return null;
        }
        final String dateStr = line.substring(endLogTypeIndex + 2, initLogMessageIndex);

        try {
            return LocalDateTime.parse(dateStr);
        } catch (final DateTimeParseException e) {
            handleLogError(e);
            return null;
        }
    }

    private LogType getLogType(final String logLine) {
        if (logLine.startsWith(LOG_TYPE_INIT_CHARACTER) && logLine.length() >= 6 && List.of("E", "I", "W").contains(logLine.substring(1,2))) {
            final int endLogTypeIndex = logLine.indexOf(LOG_TYPE_END_CHAR);

            try {
                return LogType.valueOf(logLine.substring(1, endLogTypeIndex));
            } catch (IllegalArgumentException exception) {
                handleLogError(exception);
                return null;
            }
        }
        return null;
    }
}
