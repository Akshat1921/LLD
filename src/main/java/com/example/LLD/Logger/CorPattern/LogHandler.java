package com.example.LLD.Logger.CorPattern;

import com.example.LLD.Logger.LogAppenderStrategies.LogAppender;
import com.example.LLD.Logger.enums.LogLevel;
import com.example.LLD.Logger.models.LogMessage;

public abstract class LogHandler {
    public static int INFO = 1;
    public static int DEBUG = 2;
    public static int ERROR = 3;
    
    protected int level;
    protected LogHandler nextLogger;
    protected LogAppender appender;

    public LogHandler(int level, LogAppender appender){
        this.level = level;
        this.appender = appender;
    }

    public void setNextLogger(LogHandler nextLogger){
        this.nextLogger = nextLogger;
    }

    public void logMessage(int level, String message){
        if(this.level>=level){
            LogLevel logLevel = intTLogLevel(level);
            LogMessage logMessage = new LogMessage(logLevel, message);
            if(appender!=null){
                appender.append(logMessage);
            }
            write(message);
        }else if(nextLogger!=null){
            nextLogger.logMessage(level, message);
        }
    }

    private LogLevel intTLogLevel(int level){
        switch (level) {
            case 1:
                return LogLevel.INFO;
            case 2:
                return LogLevel.DEBUG;
            case 3:
                return LogLevel.ERROR;
            default:
                return LogLevel.INFO;
        }
    }

    protected abstract void write(String message);

}
