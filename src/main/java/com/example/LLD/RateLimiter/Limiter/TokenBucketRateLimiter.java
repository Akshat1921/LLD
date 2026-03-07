package com.example.LLD.RateLimiter.Limiter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.example.LLD.RateLimiter.Enums.RateLimitType;
import com.example.LLD.RateLimiter.Model.RateLimitConfig;

public class TokenBucketRateLimiter extends RateLimiter{
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRefillTime = new HashMap<>();

    public TokenBucketRateLimiter(RateLimitConfig config) {
        super(config, RateLimitType.TOKEN_BUCKET);
    }

    @Override
    public boolean allowRequest(String userId) {
        AtomicBoolean allowed = new AtomicBoolean(false);
        long now = System.currentTimeMillis();

        tokens.compute(userId, (id, availableTokens)->{
            int currentTokens = refillTokens(userId, now);
            if(currentTokens>0){
                allowed.set(true);
                return currentTokens-1;
            }else{
                return currentTokens;
            }
        });
        return allowed.get();
    }

    public int refillTokens(String id, long now){
        double refillRate = (double) config.getWindowsInSeconds()/config.getMaxRequests();
        long lastRefill = lastRefillTime.getOrDefault(id, now);
        long timeElapsed = (lastRefill-now)/1000;
        int refillTokens = (int) (timeElapsed/refillRate);
        
        int currentTokens = tokens.getOrDefault(id, config.getMaxRequests());
        currentTokens = Math.min(config.getMaxRequests(), refillTokens+currentTokens);

        if(refillRate>0){
            lastRefillTime.put(id, now);
        }
        return currentTokens;
    }

}
