# Rate Limiter - Low Level Design Interview

## Problem Statement

Design a **Rate Limiting System** that controls request rates using multiple algorithms (Token Bucket, Fixed Window, Leaky Bucket, Sliding Window Log) with support for **user tiers** and **concurrent access**.

**Core Features**:
- Multiple rate limiting algorithms
- Per-user tier configuration (FREE, PREMIUM)
- Thread-safe concurrent request handling
- Factory-based algorithm selection

---

## Requirements

### Functional Requirements
1. **Rate Limiting Algorithms**: Token Bucket, Fixed Window, Leaky Bucket, Sliding Window Log
2. **User Tiers**: Different limits per tier (FREE: 10 req/min, PREMIUM: 100 req/min)
3. **Request Validation**: Check if user can make request based on tier and algorithm
4. **Configuration**: Configurable max requests and time window
5. **Per-User Tracking**: Independent rate limits per userId

### Non-Functional Requirements
1. **Concurrency**: Thread-safe using ConcurrentHashMap and atomic operations
2. **Performance**: O(1) for most algorithms (except Sliding Window Log)
3. **Extensibility**: Easy to add new algorithms and tiers
4. **Accuracy**: Precise request counting and window management

---

## Core Entities

| Entity | Description | Key Attributes |
|--------|-------------|----------------|
| **RateLimiter** | Abstract rate limiter | config, type, allowRequest() |
| **RateLimitConfig** | Rate limit settings | maxRequests, windowsInSeconds |
| **User** | User with tier | userId, tier |
| **RateLimiterService** | Manages per-tier limiters | rateLimiters: Map<UserTier, RateLimiter> |
| **RateLimitType** | Algorithm enum | TOKEN_BUCKET, FIXED_WINDOW, etc. |
| **UserTier** | User tier enum | FREE, PREMIUM |

---

## Design Patterns

### 1. **Strategy Pattern** ⭐
**Purpose**: Multiple rate limiting algorithms with common interface

**Implementation**:
```java
public abstract class RateLimiter {
    protected final RateLimitConfig config;
    protected final RateLimitType type;
    
    public abstract boolean allowRequest(String userId);
}

// Strategy 1: Token Bucket
public class TokenBucketRateLimiter extends RateLimiter {
    private final Map<String, Integer> tokens = new ConcurrentHashMap<>();
    private final Map<String, Long> lastRefillTime = new HashMap<>();
    
    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis();
        int currentTokens = refillTokens(userId, now);
        
        if (currentTokens > 0) {
            tokens.put(userId, currentTokens - 1);
            return true;
        }
        return false;
    }
    
    private int refillTokens(String userId, long now) {
        // Calculate tokens based on elapsed time
        double refillRate = (double) config.getWindowsInSeconds() / config.getMaxRequests();
        long lastRefill = lastRefillTime.getOrDefault(userId, now);
        int refillTokens = (int) ((now - lastRefill) / 1000 / refillRate);
        
        int currentTokens = tokens.getOrDefault(userId, config.getMaxRequests());
        return Math.min(config.getMaxRequests(), currentTokens + refillTokens);
    }
}

// Strategy 2: Fixed Window
public class FixedWindowRateLimiter extends RateLimiter {
    private final Map<String, Integer> requestCount = new ConcurrentHashMap<>();
    private final Map<String, Long> windowStart = new HashMap<>();
    
    public boolean allowRequest(String userId) {
        long currentWindow = System.currentTimeMillis() / 1000 / config.getWindowsInSeconds();
        long lastWindow = windowStart.getOrDefault(userId, currentWindow);
        
        if (lastWindow != currentWindow) {
            // New window - reset counter
            windowStart.put(userId, currentWindow);
            requestCount.put(userId, 1);
            return true;
        }
        
        int count = requestCount.getOrDefault(userId, 0);
        if (count < config.getMaxRequests()) {
            requestCount.put(userId, count + 1);
            return true;
        }
        return false;
    }
}

// Similarly: LeakyBucketRateLimiter, SlidingWindowLogRateLimiter
```

**Benefits**: ✅ Pluggable algorithms ✅ Easy to add new strategies ✅ Runtime selection

### 2. **Factory Pattern** ⭐
**Purpose**: Create rate limiter instances based on algorithm type

**Implementation**:
```java
public class RateLimiterFactory {
    public static RateLimiter createRateLimiter(RateLimitType algo, 
                                                RateLimitConfig config) {
        return switch (algo) {
            case TOKEN_BUCKET -> new TokenBucketRateLimiter(config);
            case FIXED_WINDOW -> new FixedWindowRateLimiter(config);
            case LEAKY_BUCKET -> new LeakyBucketRateLimiter(config);
            case SLIDING_WINDOW_LOG -> new SlidingWindowLogRateLimiter(config);
            default -> throw new IllegalArgumentException("Unknown type: " + algo);
        };
    }
}
```

**Benefits**: ✅ Centralized creation ✅ Decouples client from concrete classes

### 3. **Template Method Pattern**
**Purpose**: Abstract base class defines structure, subclasses implement algorithm

**Implementation**:
```java
public abstract class RateLimiter {
    protected final RateLimitConfig config;
    
    // Template method - subclasses must implement
    public abstract boolean allowRequest(String userId);
}
```

**Benefits**: ✅ Code reuse ✅ Consistent interface

---

## Class Diagram

See [RateLimiter_UML.drawio](diagrams/RateLimiter_UML.drawio)

**Key Relationships**:
- RateLimiter ←── TokenBucketRateLimiter, FixedWindowRateLimiter, LeakyBucketRateLimiter, SlidingWindowLogRateLimiter
- RateLimiterFactory → creates → RateLimiter
- RateLimiterService ◆→ Map<UserTier, RateLimiter>
- RateLimiter ◆→ RateLimitConfig

---

## Code Walkthrough

### Complete Request Flow

```java
// 1. Setup service with per-tier configuration
RateLimiterService service = new RateLimiterService();
// FREE: TokenBucket (10 req/60s)
// PREMIUM: FixedWindow (100 req/60s)

// 2. Create users
User freeUser = new User("user1", UserTier.FREE);
User premiumUser = new User("user2", UserTier.PREMIUM);

// 3. Make requests
for (int i = 1; i <= 15; i++) {
    boolean allowed = service.allowRequest(freeUser);
    System.out.println("Request " + i + ": " + (allowed ? "ALLOWED" : "BLOCKED"));
}
// Output: 10 ALLOWED, 5 BLOCKED (10 req/min limit)

for (int i = 1; i <= 120; i++) {
    boolean allowed = service.allowRequest(premiumUser);
}
// Output: 100 ALLOWED, 20 BLOCKED (100 req/min limit)
```

### Key Implementation Details

**RateLimiterService - Per-tier configuration**:
```java
public class RateLimiterService {
    private final Map<UserTier, RateLimiter> rateLimiters = new HashMap<>();
    
    public RateLimiterService() {
        // FREE tier: TokenBucket (10 req/60s)
        rateLimiters.put(UserTier.FREE,
            RateLimiterFactory.createRateLimiter(
                RateLimitType.TOKEN_BUCKET,
                new RateLimitConfig(10, 60)
            )
        );
        
        // PREMIUM tier: FixedWindow (100 req/60s)
        rateLimiters.put(UserTier.PREMIUM,
            RateLimiterFactory.createRateLimiter(
                RateLimitType.FIXED_WINDOW,
                new RateLimitConfig(100, 60)
            )
        );
    }
    
    public boolean allowRequest(User user) {
        RateLimiter limiter = rateLimiters.get(user.getTier());
        return limiter.allowRequest(user.getUserId());
    }
}
```

**Leaky Bucket - Constant leak rate**:
```java
public class LeakyBucketRateLimiter extends RateLimiter {
    private static class Bucket {
        long currentWater = 0;
        long lastLeakTimestamp = System.currentTimeMillis();
    }
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final long leakRate;  // requests per second
    
    public boolean allowRequest(String userId) {
        Bucket bucket = buckets.computeIfAbsent(userId, id -> new Bucket());
        
        synchronized (bucket) {
            leak(bucket);  // Remove leaked water
            
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
```

**Sliding Window Log - Timestamp tracking**:
```java
public class SlidingWindowLogRateLimiter extends RateLimiter {
    private final Map<String, Queue<Long>> requestLog = new ConcurrentHashMap<>();
    
    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis() / 1000;
        
        Queue<Long> log = requestLog.computeIfAbsent(userId, id -> new ArrayDeque<>());
        
        // Remove requests outside window
        while (!log.isEmpty() && (now - log.peek()) >= config.getWindowsInSeconds()) {
            log.poll();
        }
        
        if (log.size() < config.getMaxRequests()) {
            log.add(now);
            return true;
        }
        return false;
    }
}
```

---

## Design Decisions

### 1. Why Strategy Pattern?
**Problem**: Multiple algorithms with different behaviors  
**Solution**: Common interface, pluggable implementations  
**Trade-off**: ✅ Flexible, ❌ More classes

### 2. Why ConcurrentHashMap + AtomicBoolean?
**Problem**: Concurrent requests for same user  
**Solution**: Thread-safe map with atomic operations  
**Alternative**: Synchronized methods (simpler but slower)

### 3. Why separate RateLimitConfig?
**Design**: Decouple configuration from algorithm  
**Benefits**: Reusable across algorithms, easy to modify

### 4. Algorithm Choice per Tier?
**FREE**: TokenBucket (allows bursts, smoother)  
**PREMIUM**: FixedWindow (simpler, higher limits)  
**Flexibility**: Can change per business needs

---

## Extensibility

### Adding Sliding Window Counter
```java
public class SlidingWindowCounterRateLimiter extends RateLimiter {
    private Map<String, Integer> currentWindowCount = new ConcurrentHashMap<>();
    private Map<String, Integer> previousWindowCount = new HashMap<>();
    
    public boolean allowRequest(String userId) {
        long now = System.currentTimeMillis() / 1000;
        long currentWindow = now / config.getWindowsInSeconds();
        
        // Weighted count: previous * (1 - elapsed%) + current
        // More accurate than FixedWindow, less memory than SlidingLog
        return /* weighted calculation */;
    }
}
```

### Adding Enterprise Tier
```java
public enum UserTier {
    FREE, PREMIUM, ENTERPRISE
}

public RateLimiterService() {
    rateLimiters.put(UserTier.ENTERPRISE,
        RateLimiterFactory.createRateLimiter(
            RateLimitType.TOKEN_BUCKET,
            new RateLimitConfig(1000, 60)  // 1000 req/min
        )
    );
}
```

### Adding Distributed Rate Limiting
```java
public class RedisRateLimiter extends RateLimiter {
    private final RedisClient redis;
    
    public boolean allowRequest(String userId) {
        String key = "ratelimit:" + userId;
        Long count = redis.incr(key);
        
        if (count == 1) {
            redis.expire(key, config.getWindowsInSeconds());
        }
        
        return count <= config.getMaxRequests();
    }
}
```

### Adding Rate Limit Headers
```java
public class RateLimitResponse {
    private boolean allowed;
    private int remaining;
    private long resetTime;
}

public abstract class RateLimiter {
    public abstract RateLimitResponse allowRequestWithMetrics(String userId);
}
```

---

## Complexity Analysis

| Algorithm | Time (allowRequest) | Space (per user) | Accuracy |
|-----------|---------------------|------------------|----------|
| **Token Bucket** | O(1) | O(1) | High |
| **Fixed Window** | O(1) | O(1) | Medium (boundary burst) |
| **Leaky Bucket** | O(1) | O(1) | High (smooth) |
| **Sliding Window Log** | O(n) | O(n) | Very High |

*n = number of requests in window*

### Algorithm Comparison

| Algorithm | Pros | Cons | Use Case |
|-----------|------|------|----------|
| **Token Bucket** | Allows bursts, simple | Complex refill logic | API rate limiting |
| **Fixed Window** | Very simple, low memory | Boundary burst issue | Simple quotas |
| **Leaky Bucket** | Smooth traffic, no bursts | May reject valid bursts | Traffic shaping |
| **Sliding Window Log** | Most accurate | High memory, O(n) time | Critical systems |

### Scalability
- **Single Server**: ConcurrentHashMap sufficient
- **Distributed**: Use Redis with Lua scripts for atomicity
- **High Traffic**: Approximate algorithms (Sliding Window Counter)

---

## Testing Strategy

### Unit Tests
```java
@Test
void testTokenBucket() {
    RateLimitConfig config = new RateLimitConfig(10, 60);
    RateLimiter limiter = new TokenBucketRateLimiter(config);
    
    // First 10 requests should pass
    for (int i = 0; i < 10; i++) {
        assertTrue(limiter.allowRequest("user1"));
    }
    
    // 11th request should fail
    assertFalse(limiter.allowRequest("user1"));
}

@Test
void testFixedWindowReset() {
    RateLimitConfig config = new RateLimitConfig(5, 1);  // 5 req/sec
    RateLimiter limiter = new FixedWindowRateLimiter(config);
    
    for (int i = 0; i < 5; i++) {
        assertTrue(limiter.allowRequest("user1"));
    }
    assertFalse(limiter.allowRequest("user1"));
    
    Thread.sleep(1100);  // Wait for window reset
    assertTrue(limiter.allowRequest("user1"));
}

@Test
void testConcurrentRequests() throws Exception {
    RateLimiterService service = new RateLimiterService();
    User user = new User("user1", UserTier.FREE);
    
    ExecutorService executor = Executors.newFixedThreadPool(20);
    AtomicInteger allowed = new AtomicInteger(0);
    
    for (int i = 0; i < 20; i++) {
        executor.submit(() -> {
            if (service.allowRequest(user)) {
                allowed.incrementAndGet();
            }
        });
    }
    
    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.SECONDS);
    
    assertEquals(10, allowed.get());  // Only 10 should be allowed
}
```

### Integration Test
```java
@Test
void testPerTierLimits() {
    RateLimiterService service = new RateLimiterService();
    User freeUser = new User("free", UserTier.FREE);
    User premiumUser = new User("premium", UserTier.PREMIUM);
    
    int freeAllowed = 0, premiumAllowed = 0;
    
    for (int i = 0; i < 150; i++) {
        if (service.allowRequest(freeUser)) freeAllowed++;
        if (service.allowRequest(premiumUser)) premiumAllowed++;
    }
    
    assertEquals(10, freeAllowed);
    assertEquals(100, premiumAllowed);
}
```

### Edge Cases
- Clock skew, Concurrent access, Boundary conditions, Integer overflow, Empty userId

---

## Follow-up Questions

### Q1: How to handle distributed rate limiting?
**Answer**: Use Redis with atomic operations (INCR + EXPIRE), or Lua scripts for complex logic. For extreme scale, use approximate algorithms or sharding.

### Q2: How to prevent boundary burst in Fixed Window?
**Answer**: Use Sliding Window Counter - weighted combination of current and previous window counts.

### Q3: How to handle clock skew in distributed systems?
**Answer**: Use logical clocks (Lamport timestamps) or NTP synchronization. Prefer time-independent algorithms or add tolerance margins.

### Q4: How to implement per-API endpoint limits?
**Answer**: Composite key: `userId + endpoint`. Example: "user123:/api/search" → separate limits per endpoint.

### Q5: How to support dynamic limit updates?
**Answer**: External config service (e.g., Spring Cloud Config), hot reload on change, graceful transition between limits.

### Q6: How to optimize memory for Sliding Window Log?
**Answer**: Use Sliding Window Counter (hybrid), or circular buffer with fixed size, or probabilistic data structures (Count-Min Sketch).

### Q7: How to implement rate limit bypass for admin users?
**Answer**: Special tier (ADMIN) with unlimited config, or check `user.isAdmin()` before rate limit, or separate limiter service.

### Q8: How to scale for 1M+ requests/second?
**Answer**: Distributed Redis cluster with consistent hashing, approximate algorithms (Sliding Window Counter), rate limiting at edge (CDN/API Gateway), client-side rate limiting with tokens
