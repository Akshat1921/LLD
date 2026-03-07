package com.example.LLD.PubSubEasy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.LLD.PubSubEasy.models.Message;
import com.example.LLD.PubSubEasy.Subscriber.Subscriber;
import com.example.LLD.PubSubEasy.models.Topic;

public class PubSubBroker {
    private final Map<String, Topic> topics;
    private final ExecutorService executor;

    public PubSubBroker(){
        this.topics = new ConcurrentHashMap<>();
        executor = Executors.newCachedThreadPool();
    }

    public Topic createTopic(String name){
        Topic topic = new Topic(name);
        topics.put(name, topic);
        return topic;
    }

    public void subscribe(String topicName, Subscriber subscriber){
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicName);
        }
        topic.subscribe(subscriber);
    }

    public void publish(String topicName, Message message){
        Topic topic = topics.get(topicName);
        if (topic == null) {
            throw new IllegalArgumentException("Topic not found: " + topicName);
        }
        for(Subscriber subscriber: topic.getSubscribers()){
            executor.submit(()->subscriber.onMessage(message));
        }
    }

    public void shutdown(){
        executor.shutdown();
    }

}
