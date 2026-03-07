package com.example.LLD.Logger.CorPattern.ConcreteLogHandlers;

import com.example.LLD.Logger.CorPattern.LogHandler;
import com.example.LLD.Logger.LogAppenderStrategies.LogAppender;

public class ErrorLogger extends LogHandler{
    public ErrorLogger(int level, LogAppender appender) {
        super(level, appender);
    }

    @Override
    protected void write(String message) {
        System.out.println("DEBUG: " + message);
    }
}
