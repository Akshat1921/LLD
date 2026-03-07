package com.example.LLD.NotificationSystem.Model;

import java.util.Set;

import com.example.LLD.NotificationSystem.Enums.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPreference {
    private String userId;
    private Set<ChannelType> preferredChannel;
}
