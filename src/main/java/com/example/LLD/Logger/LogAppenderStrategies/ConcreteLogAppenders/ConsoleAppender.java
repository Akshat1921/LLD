package com.example.LLD.Logger.LogAppenderStrategies.ConcreteLogAppenders;

import com.example.LLD.Logger.LogAppenderStrategies.LogAppender;
import com.example.LLD.Logger.models.LogMessage;

public class ConsoleAppender implements LogAppender{
    @Override
    public void append(LogMessage logMessage){
        System.out.println(logMessage);
    }
}
