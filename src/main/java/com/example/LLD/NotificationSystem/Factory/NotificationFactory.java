package com.example.LLD.NotificationSystem.Factory;

import com.example.LLD.NotificationSystem.Channels.EmailNotificationChannel;
import com.example.LLD.NotificationSystem.Channels.NotificationChannel;
import com.example.LLD.NotificationSystem.Channels.PushNotificationChannel;
import com.example.LLD.NotificationSystem.Channels.SMSNotificationChannel;
import com.example.LLD.NotificationSystem.Enums.ChannelType;

public class NotificationFactory {
    public static NotificationChannel getChannel(ChannelType channelType){
        switch (channelType) {
            case SMS:
                return new SMSNotificationChannel();
            case EMAIL:
                return new EmailNotificationChannel();
            case PUSH:
                return new PushNotificationChannel();
            default:
                return null;
        }
    }
}
