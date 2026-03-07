package com.example.LLD.RateLimiter.Factory;

import com.example.LLD.RateLimiter.Enums.RateLimitType;
import com.example.LLD.RateLimiter.Limiter.FixedWindowRateLimiter;
import com.example.LLD.RateLimiter.Limiter.RateLimiter;
import com.example.LLD.RateLimiter.Limiter.SlindingWindowLogRateLimiter;
import com.example.LLD.RateLimiter.Limiter.TokenBucketRateLimiter;
import com.example.LLD.RateLimiter.Model.RateLimitConfig;

public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(RateLimitType algo, RateLimitConfig config){
        switch(algo){
            case TOKEN_BUCKET:
                return new TokenBucketRateLimiter(config);
            case FIXED_WINDOW:
                return new FixedWindowRateLimiter(config);
            case SLIDING_WINDOW_LOG:
                return new SlindingWindowLogRateLimiter(config);
            default:
                throw new IllegalArgumentException("Unknown rate limit type: " + algo);
        }
    }
}
