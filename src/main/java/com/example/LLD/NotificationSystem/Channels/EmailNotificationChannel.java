package com.example.LLD.NotificationSystem.Channels;

import com.example.LLD.NotificationSystem.Model.Notification;

public class EmailNotificationChannel implements NotificationChannel{

    @Override
    public void send(Notification notification) {
        System.out.println("Sending Email to user " + notification.getUserId() + ": " + notification.getMessage());
    }
    
}
