package com.example.LLD.NotificationSystem.Channels;

import com.example.LLD.NotificationSystem.Model.Notification;

public interface NotificationChannel {
    void send(Notification notification);
}
