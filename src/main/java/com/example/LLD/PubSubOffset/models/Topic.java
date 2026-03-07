package com.example.LLD.PubSubOffset.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a named topic that holds an ordered list of messages
 * and tracks all subscribers attached to it.
 *
 * Thread-safety:
 * - The messages list is synchronized so publishers and consumers can
 * operate concurrently.
 */
public class Topic {
    private final String name;
    private final List<Message> messages;
    private final List<TopicSubscriber> subscribers;

    public Topic(String name) {
        this.name = name;
        this.messages = Collections.synchronizedList(new ArrayList<>());
        this.subscribers = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public List<TopicSubscriber> getSubscribers() {
        return subscribers;
    }

    /**
     * Adds a message and wakes up all subscribers so they can consume it.
     */
    public void addMessage(Message message) {
        messages.add(message);
        // Notify every subscriber that a new message is available
        for (TopicSubscriber topicSubscriber : subscribers) {
            topicSubscriber.wakeup();
        }
    }

    /**
     * Registers a TopicSubscriber and starts its consumer thread.
     */
    public void addSubscriber(TopicSubscriber topicSubscriber) {
        subscribers.add(topicSubscriber);
        // The service layer will handle starting the consumer

    }
}
