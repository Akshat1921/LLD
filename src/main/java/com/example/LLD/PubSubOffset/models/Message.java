package com.example.LLD.PubSubOffset.models;

/**
 * Immutable message that flows through the Pub-Sub system.
 */
public class Message {
    private final String content;

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
