package com.example.LLD.PubSubEasy.Subscriber;

import com.example.LLD.PubSubEasy.models.Message;

public interface Subscriber {
    String getId();
    void onMessage(Message message);
}
