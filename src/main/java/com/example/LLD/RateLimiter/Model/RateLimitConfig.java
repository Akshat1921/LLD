package com.example.LLD.RateLimiter.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RateLimitConfig {
    private final int maxRequests;
    private final int windowsInSeconds;
}
