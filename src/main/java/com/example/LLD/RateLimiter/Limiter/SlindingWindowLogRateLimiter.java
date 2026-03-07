package com.example.LLD.RateLimiter.Limiter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.LLD.RateLimiter.Enums.RateLimitType;
import com.example.LLD.RateLimiter.Model.RateLimitConfig;

public class SlindingWindowLogRateLimiter extends RateLimiter{
    private final Map<String, Queue<Long>> requestLog = new ConcurrentHashMap<>();

    public SlindingWindowLogRateLimiter(RateLimitConfig config){
        super(config, RateLimitType.SLIDING_WINDOW_LOG);
    }

    @Override
    public boolean allowRequest(String userId){
        AtomicBoolean allowed = new AtomicBoolean(false);
        long now = System.currentTimeMillis()/1000;

        requestLog.compute(userId, (id, log)->{
            if(log==null){
                log = new ArrayDeque<>();
            }
            while(!log.isEmpty() && (now-log.peek())>=config.getWindowsInSeconds()){
                log.poll();
            }
            if(log.size()<config.getMaxRequests()){
                log.add(now);
                allowed.set(true);
            }
            return log;
        });
        return allowed.get();
    }
}
