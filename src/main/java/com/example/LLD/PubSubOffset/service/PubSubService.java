package com.example.LLD.PubSubOffset.service;

import com.example.LLD.PubSubOffset.models.ISubscriber;
import com.example.LLD.PubSubOffset.models.Message;
import com.example.LLD.PubSubOffset.models.Topic;
import com.example.LLD.PubSubOffset.models.TopicSubscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Central service that manages topics, subscriptions, and message publishing.
 *
 * Usage pattern:
 * 1. createTopic("orders")
 * 2. subscribe(subscriber, topic)
 * 3. publish(topic, message)
 */
public class PubSubService {
    private final Map<String, Topic> topics;
    private final ExecutorService executorService;

    public PubSubService() {
        this.topics = new HashMap<>();
        // Use a cached thread pool to mimic the previous behavior (one thread per
        // subscriber)
        // but with better resource management.
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Creates a new topic with the given name.
     * 
     * @return the created Topic
     * @throws IllegalArgumentException if a topic with the same name already exists
     */
    public Topic createTopic(String topicName) {
        if (topics.containsKey(topicName)) {
            throw new IllegalArgumentException("Topic already exists: " + topicName);
        }
        Topic topic = new Topic(topicName);
        topics.put(topicName, topic);
        System.out.println("Created topic: " + topicName);
        return topic;
    }

    /**
     * Subscribes the given subscriber to a topic.
     * A dedicated consumer task is submitted to the executor service.
     */
    public void subscribe(ISubscriber subscriber, Topic topic) {
        TopicSubscriber topicSubscriber = new TopicSubscriber(subscriber);
        topic.addSubscriber(topicSubscriber);
        topicSubscriber.startConsuming(topic, executorService);
        System.out.println("Subscriber [" + subscriber.getId() + "] subscribed to topic: "
                + topic.getName());
    }

    /**
     * Publishes a message to the given topic.
     * All subscribers to this topic will be notified.
     */
    public void publish(Topic topic, Message message) {
        topic.addMessage(message);
        System.out.println("Published to [" + topic.getName() + "]: " + message.getContent());
    }

    /**
     * Retrieves a topic by name.
     * 
     * @return the Topic, or null if not found
     */
    public Topic getTopic(String topicName) {
        return topics.get(topicName);
    }

    /**
     * Gracefully shuts down the executor service.
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
