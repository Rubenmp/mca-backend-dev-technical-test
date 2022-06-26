package com.mca.yourapp.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Log {
    private LogType type;
    private LocalDateTime date;
    private String message;
}
