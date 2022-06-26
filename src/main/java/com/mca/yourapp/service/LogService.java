package com.mca.yourapp.service;

import com.mca.yourapp.service.dto.Log;
import com.mca.yourapp.service.dto.LogType;

import java.util.List;


public interface LogService {
    void log(final LogType type, final String message);

    void log(final Exception exception);

    List<Log> getLogs();
}
