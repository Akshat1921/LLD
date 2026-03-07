package com.example.LLD.RateLimiter.Limiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.example.LLD.RateLimiter.Enums.RateLimitType;
import com.example.LLD.RateLimiter.Model.RateLimitConfig;

public class LeakyBucketRateLimiter extends RateLimiter {

    private static class Bucket {
        long currentWater = 0;
        long lastLeakTimestamp = System.currentTimeMillis();
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final long leakRate; // requests per second

    public LeakyBucketRateLimiter(RateLimitConfig config) {
        super(config, RateLimitType.LEAKY_BUCKET);

        // leak rate = maxRequests / windowInSeconds
        this.leakRate = config.getMaxRequests() / config.getWindowsInSeconds();
    }

    @Override
    public boolean allowRequest(String userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, id -> new Bucket());

        synchronized (bucket) {
            leak(bucket);

            if (bucket.currentWater < config.getMaxRequests()) {
                bucket.currentWater++;
                return true;
            }

            return false;
        }
    }

    private void leak(Bucket bucket) {
        long now = System.currentTimeMillis();
        long elapsedMs = now - bucket.lastLeakTimestamp;

        long leaked = (elapsedMs * leakRate) / 1000;

        if (leaked > 0) {
            bucket.currentWater = Math.max(0, bucket.currentWater - leaked);
            bucket.lastLeakTimestamp = now;
        }
    }
}