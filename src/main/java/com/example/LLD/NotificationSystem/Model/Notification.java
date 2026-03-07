package com.example.LLD.NotificationSystem.Model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Notification {
    private String userId;
    private String message;
}
