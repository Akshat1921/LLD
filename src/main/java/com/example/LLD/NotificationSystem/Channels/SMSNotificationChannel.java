package com.example.LLD.NotificationSystem.Channels;

import com.example.LLD.NotificationSystem.Model.Notification;

public class SMSNotificationChannel implements NotificationChannel{
    @Override
    public void send(Notification notification) {
        System.out.println("Sending SMS to user " + notification.getUserId() + ": " + notification.getMessage());
    }
}
