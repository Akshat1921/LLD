package com.example.LLD.Logger;

import com.example.LLD.Logger.CorPattern.LogHandler;
import com.example.LLD.Logger.CorPattern.ConcreteLogHandlers.DebugLogger;
import com.example.LLD.Logger.CorPattern.ConcreteLogHandlers.ErrorLogger;
import com.example.LLD.Logger.CorPattern.ConcreteLogHandlers.InfoLogger;
import com.example.LLD.Logger.LogAppenderStrategies.LogAppender;
import com.example.LLD.Logger.LogAppenderStrategies.ConcreteLogAppenders.ConsoleAppender;
import com.example.LLD.Logger.LogAppenderStrategies.ConcreteLogAppenders.FileAppender;
import com.example.LLD.Logger.enums.LogLevel;
import com.example.LLD.Logger.loggerControllers.LogConfig;
import com.example.LLD.Logger.loggerControllers.Logger;

public class Main {
    private static LogHandler getChainOfLoggers(LogAppender logAppender){
        LogHandler debugLogger = new DebugLogger(LogHandler.DEBUG, logAppender);
        LogHandler infoLogger = new InfoLogger(LogHandler.INFO, logAppender);
        LogHandler erorrLogger = new ErrorLogger(LogHandler.ERROR, logAppender);
        infoLogger.setNextLogger(debugLogger);
        debugLogger.setNextLogger(erorrLogger);
        return infoLogger;
    }

    public static void main(String[] args) {
            // Select the log appender (console or file)
            LogAppender consoleAppender = new ConsoleAppender();
            LogAppender fileAppender = new FileAppender("logs.txt");
            // Create the chain of loggers with the console appender
            LogHandler loggerChain = getChainOfLoggers(consoleAppender);

            // Use a single logging approach to avoid duplication
            System.out.println("Logging INFO level message:");
            loggerChain.logMessage(LogHandler.INFO, "This is an information.");
            System.out.println("\nLogging DEBUG level message:");
            loggerChain.logMessage(LogHandler.DEBUG, "This is a debug level information.");
            System.out.println("\nLogging ERROR level message:");
            loggerChain.logMessage(LogHandler.ERROR, "This is an error information.");

            // Demonstrate the singleton Logger usage as an alternative
            System.out.println("\nUsing Singleton Logger:");
            Logger logger = Logger.getInstance(LogLevel.INFO, consoleAppender);
            logger.setConfig(new LogConfig(LogLevel.INFO, fileAppender));
            logger.error("Using singleton Logger - Error message");
    }
}
