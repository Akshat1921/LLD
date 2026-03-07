package com.example.LLD.RateLimiter.Limiter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.LLD.RateLimiter.Enums.RateLimitType;
import com.example.LLD.RateLimiter.Model.RateLimitConfig;

public class FixedWindowRateLimiter extends RateLimiter{
    private final Map<String, Integer> requestCount = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStart = new HashMap<>();

    public FixedWindowRateLimiter(RateLimitConfig config){
        super(config, RateLimitType.FIXED_WINDOW);
    }

    @Override
    public boolean allowRequest(String userId){
        AtomicBoolean allowed = new AtomicBoolean(false);
        long currentReqWindow = System.currentTimeMillis()/1000/config.getWindowsInSeconds();

        requestCount.compute(userId, (id, count)->{
            long lastReqWindow = windowStart.getOrDefault(userId, currentReqWindow);
            
            if(lastReqWindow!=currentReqWindow){
                // window expired -> reset counter and window of last req
                windowStart.put(id, currentReqWindow);
                allowed.set(true);
                count = 1;
            }

            if(count==null) count = 0;

            if(count<config.getMaxRequests()){
                allowed.set(true);
                count++;
            }
            return count;
        });
        return allowed.get();
    }

}
