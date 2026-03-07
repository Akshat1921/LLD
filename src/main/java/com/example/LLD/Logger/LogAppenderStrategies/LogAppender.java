package com.example.LLD.Logger.LogAppenderStrategies;

import com.example.LLD.Logger.models.LogMessage;

public interface LogAppender {
    void append(LogMessage logMessage);
}
