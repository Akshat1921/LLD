package com.example.LLD.NotificationSystem.Service;

import java.util.Set;

import com.example.LLD.NotificationSystem.Channels.NotificationChannel;
import com.example.LLD.NotificationSystem.Enums.ChannelType;
import com.example.LLD.NotificationSystem.Factory.NotificationFactory;
import com.example.LLD.NotificationSystem.Model.Notification;
import com.example.LLD.NotificationSystem.Model.UserPreference;

public class NotificationDispatcher {
    private final UserPreferenceService preferenceService;

    public NotificationDispatcher(UserPreferenceService userPreferenceService){
        this.preferenceService = userPreferenceService;
    }

    public void dispatch(Notification notification){
        UserPreference preference = preferenceService.getPreference(notification.getUserId());
        Set<ChannelType> channels = preference.getPreferredChannel();
        
        for(ChannelType channelType: channels){
            NotificationChannel channel = NotificationFactory.getChannel(channelType);
            channel.send(notification);
        }

    }

}
