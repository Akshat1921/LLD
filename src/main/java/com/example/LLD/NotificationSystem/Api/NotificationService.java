package com.example.LLD.NotificationSystem.Api;

import com.example.LLD.NotificationSystem.Model.Notification;
import com.example.LLD.NotificationSystem.Service.NotificationDispatcher;

public class NotificationService {
    private final NotificationDispatcher notificationDispatcher;

    public NotificationService(NotificationDispatcher notificationDispatcher){
        this.notificationDispatcher = notificationDispatcher;
    }

    public void sendNotification(Notification notification){
        notificationDispatcher.dispatch(notification);
    }

}
