package com.example.LLD.NotificationSystem;

import java.util.Set;

import com.example.LLD.NotificationSystem.Api.AsyncNotificationService;
import com.example.LLD.NotificationSystem.Api.NotificationService;
import com.example.LLD.NotificationSystem.Enums.ChannelType;
import com.example.LLD.NotificationSystem.Model.Notification;
import com.example.LLD.NotificationSystem.Model.UserPreference;
import com.example.LLD.NotificationSystem.Service.NotificationDispatcher;
import com.example.LLD.NotificationSystem.Service.UserPreferenceService;

public class Main {
    public static void main(String[] args) {
        // Defining preference service.
        UserPreferenceService preferenceService = new UserPreferenceService();

        // Defining user preference with Email and SMS as preferred channels.
        preferenceService.savePreference(
                new UserPreference(
                        "user123",
                        Set.of(ChannelType.EMAIL, ChannelType.SMS)
                )
        );

        // Defining notification dispatcher
        NotificationDispatcher dispatcher =
                new NotificationDispatcher(preferenceService);

        // Defining async service.
        AsyncNotificationService notificationService =
                new AsyncNotificationService(dispatcher);

        // Defining synchronous service.
        NotificationService service = new NotificationService(dispatcher);

        // Defining notification to send through multiple channels.
        Notification notification =
                new Notification(
                        "user123",
                        "Your order has been shipped!"
                );

        // Sending notification through synchronous service.
        service.sendNotification(notification);

        // Sending notification through asynchronous service.
        notificationService.sendNotification(notification);
    }
}
