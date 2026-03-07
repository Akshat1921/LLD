package com.example.LLD.RateLimiter.Limiter;

import com.example.LLD.RateLimiter.Enums.RateLimitType;
import com.example.LLD.RateLimiter.Model.RateLimitConfig;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class RateLimiter {
    protected final RateLimitConfig config;
    protected final RateLimitType type;

    public abstract boolean allowRequest(String userId);
}
