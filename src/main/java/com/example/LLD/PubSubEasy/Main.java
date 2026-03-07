package com.example.LLD.PubSubEasy;

import com.example.LLD.PubSubEasy.Subscriber.PrintSubscriber;
import com.example.LLD.PubSubEasy.Subscriber.Subscriber;
import com.example.LLD.PubSubEasy.models.Message;

public class Main {
    public static void main(String[] args) {
        PubSubBroker broker = new PubSubBroker();

        broker.createTopic("orders");
        broker.createTopic("payments");

        Subscriber s1 = new PrintSubscriber("S1");
        Subscriber s2 = new PrintSubscriber("S2");
        Subscriber s3 = new PrintSubscriber("S3");

        broker.subscribe("orders", s1);
        broker.subscribe("orders", s2);
        broker.subscribe("payments", s3);

        broker.publish("orders", new Message("Order created"));
        broker.publish("payments", new Message("Payment received"));
        broker.publish("orders", new Message("Order shipped"));

        broker.shutdown();
    }
}
