package com.mca.yourapp.service;

import com.mca.yourapp.service.dto.Log;
import com.mca.yourapp.service.dto.LogType;
import org.springframework.lang.NonNull;

import java.util.List;


public interface LogService {
    void log(@NonNull LogType type, @NonNull String message);

    void log(@NonNull Exception exception);

    List<Log> getLogs();
}
