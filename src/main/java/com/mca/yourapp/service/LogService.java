package com.mca.yourapp.service;

import com.mca.yourapp.service.dto.Log;
import com.mca.yourapp.service.dto.LogType;
import org.springframework.lang.NonNull;

import java.util.List;


public interface LogService {
    /**
     * Log any log type with any not null message.
     * */
    void log(@NonNull LogType type, @NonNull String message);

    /**
     * Log an error with the exception stack trace
     * */
    void log(@NonNull Exception exception);

    /**
     * Return all logs sorted by date (ascending)
     * */
    @NonNull List<Log> getLogs();
}
