package com.example.LLD.Logger.models;


import com.example.LLD.Logger.enums.LogLevel;

import lombok.Data;

@Data
public class LogMessage {
    private LogLevel logLevel;
    private String message;
    private final long timeStamp;

    public LogMessage(LogLevel logLevel, String message){
        this.logLevel = logLevel;
        this.message = message;
        this.timeStamp = System.currentTimeMillis();
    }

}
