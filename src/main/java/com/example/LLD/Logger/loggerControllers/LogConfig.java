package com.example.LLD.Logger.loggerControllers;

import com.example.LLD.Logger.LogAppenderStrategies.LogAppender;
import com.example.LLD.Logger.enums.LogLevel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogConfig {
    private LogLevel logLevel;
    private LogAppender appender;
}
