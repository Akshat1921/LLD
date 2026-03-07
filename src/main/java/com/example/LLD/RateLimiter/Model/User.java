package com.example.LLD.RateLimiter.Model;

import com.example.LLD.RateLimiter.Enums.UserTier;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class User {
    private final String userId;
    private final UserTier tier;
}
