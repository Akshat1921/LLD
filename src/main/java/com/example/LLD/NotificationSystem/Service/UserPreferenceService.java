package com.example.LLD.NotificationSystem.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.example.LLD.NotificationSystem.Enums.ChannelType;
import com.example.LLD.NotificationSystem.Model.UserPreference;

import lombok.Data;

@Data
public class UserPreferenceService {
    private Map<String, UserPreference> preferences;

    public UserPreferenceService(){
        preferences = new HashMap<>();
    }

    public void savePreference(UserPreference preference){
        preferences.put(preference.getUserId(), preference);
    }

    public UserPreference getPreference(String userId){
        return preferences.getOrDefault(
                userId,
                new UserPreference(userId, Set.of(ChannelType.EMAIL))
        );
    }

}
