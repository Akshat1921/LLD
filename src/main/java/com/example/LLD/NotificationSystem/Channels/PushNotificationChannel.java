package com.example.LLD.NotificationSystem.Channels;

import com.example.LLD.NotificationSystem.Model.Notification;

public class PushNotificationChannel implements NotificationChannel{

    @Override
    public void send(Notification notification) {
        System.out.println("Sending Push notification to user " + notification.getUserId() + ": " + notification.getMessage());
    }
    
}
