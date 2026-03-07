package com.example.LLD.PubSubEasy.models;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.LLD.PubSubEasy.Subscriber.Subscriber;

public class Topic {
    private final String name;
    private final List<Subscriber> subscribers;
    
    public Topic(String name){
        this.name = name;
        subscribers = new CopyOnWriteArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

}
