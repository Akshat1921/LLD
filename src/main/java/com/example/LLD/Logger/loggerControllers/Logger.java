package com.example.LLD.Logger.loggerControllers;

import java.util.concurrent.ConcurrentHashMap;

import com.example.LLD.Logger.LogAppenderStrategies.LogAppender;
import com.example.LLD.Logger.enums.LogLevel;
import com.example.LLD.Logger.models.LogMessage;

public class Logger {
    private static final ConcurrentHashMap<String, Logger> instances = new ConcurrentHashMap<>();
    private LogConfig config;

    private Logger(LogLevel logLevel, LogAppender logAppender){
        config = new LogConfig(logLevel, logAppender);
    }

    public static Logger getInstance(LogLevel logLevel, LogAppender logAppender){
        String key = logLevel.name() + "_" + logAppender.toString();
        return instances.computeIfAbsent(key, k->new Logger(logLevel, logAppender));
    }

    public void setConfig(LogConfig logConfig){
        synchronized(Logger.class){
            this.config = logConfig;
        }
    }

    public void log(LogLevel level, String message){
        if(level.getValue()>=config.getLogLevel().getValue()){
            LogMessage logMessage = new LogMessage(level, message);
            config.getAppender().append(logMessage);
        }
    }

    public void debug(String message){
        log(LogLevel.DEBUG, message);
    }

    public void info(String message){
        log(LogLevel.INFO, message);
    }

    public void error(String message){
        log(LogLevel.ERROR, message);
    }

}
