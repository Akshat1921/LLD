package com.example.LLD.Logger.LogAppenderStrategies.ConcreteLogAppenders;

import java.io.FileWriter;
import java.io.IOException;

import com.example.LLD.Logger.LogAppenderStrategies.LogAppender;
import com.example.LLD.Logger.models.LogMessage;

public class FileAppender implements LogAppender{

    private final String filePath;

    public FileAppender(String filePath){
        this.filePath = filePath;
    }

    @Override
    public void append(LogMessage logMessage){
        try(FileWriter writer = new FileWriter(filePath, true)){
            writer.write(logMessage.toString());
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
