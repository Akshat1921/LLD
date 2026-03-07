package com.example.LLD.PubSubOffset.models;

/**
 * Interface for any entity that wants to consume messages from a topic.
 * Open for extension — implement this to create custom subscriber behavior
 * (e.g., logging, database persistence, forwarding).
 */
public interface ISubscriber {
    String getId();
    void onMessage(Message message);
}
