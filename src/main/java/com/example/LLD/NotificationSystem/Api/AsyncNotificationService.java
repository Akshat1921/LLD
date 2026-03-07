package com.example.LLD.NotificationSystem.Api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.LLD.NotificationSystem.Model.Notification;
import com.example.LLD.NotificationSystem.Service.NotificationDispatcher;

public class AsyncNotificationService {
    private final NotificationDispatcher dispatcher;
    private final ExecutorService executorService;

    public AsyncNotificationService(NotificationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void sendNotification(Notification notification) {
        executorService.submit(() -> dispatcher.dispatch(notification));
    }
}