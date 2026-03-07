package com.example.LLD.PubSubEasy.Subscriber;

import com.example.LLD.PubSubEasy.models.Message;

public class PrintSubscriber implements Subscriber {
    private final String id;

    public PrintSubscriber(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void onMessage(Message message) {
        System.out.println("Subscriber " + id + " received: " + message.getPayload());
    }
}
