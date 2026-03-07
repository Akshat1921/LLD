package com.example.LLD.Logger.enums;

public enum LogLevel {
    DEBUG(1),
    INFO(2),
    ERROR(3);

    private final int value;
    private LogLevel(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    } 

    public boolean isGreaterOrEqual(LogLevel other){
        return this.value >= other.value;
    }

}
