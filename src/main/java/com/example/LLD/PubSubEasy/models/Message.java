package com.example.LLD.PubSubEasy.models;

public class Message {
    private final String payload;

    public Message(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
