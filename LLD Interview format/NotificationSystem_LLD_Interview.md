# Notification System - Low Level Design Interview

## Problem Statement

Design a **Multi-Channel Notification System** that sends notifications to users via their preferred channels (Email, SMS, Push) with support for **async processing** and **user preferences**.

**Core Features**:
- Send notifications via Email/SMS/Push
- User preference management (choose channels)
- Synchronous and asynchronous notification delivery
- Factory-based channel creation

---

## Requirements

### Functional Requirements
1. **Multi-Channel Support**: Email, SMS, Push notifications
2. **User Preferences**: Users choose preferred channels
3. **Notification Dispatch**: Send to all preferred channels
4. **Sync/Async Delivery**: Support both synchronous and asynchronous (thread pool)
5. **Default Fallback**: Email as default if no preference set

### Non-Functional Requirements
1. **Extensibility**: Easy to add new channels (WhatsApp, Slack)
2. **Scalability**: Async processing for high throughput
3. **Flexibility**: Runtime channel selection based on preferences
4. **Performance**: Non-blocking async notifications

---

## Core Entities

| Entity | Description | Key Attributes |
|--------|-------------|----------------|
| **Notification** | Message to send | userId, message |
| **UserPreference** | User's channel choices | userId, preferredChannel: Set<ChannelType> |
| **NotificationChannel** | Channel interface | send(Notification) |
| **ChannelType** | Enum for channels | EMAIL, SMS, PUSH |
| **NotificationDispatcher** | Core dispatch logic | preferenceService, dispatch() |
| **NotificationFactory** | Creates channels | getChannel(ChannelType) |

---

## Design Patterns

### 1. **Strategy Pattern** ⭐
**Purpose**: Multiple notification channels with pluggable implementations

**Implementation**:
```java
public interface NotificationChannel {
    void send(Notification notification);
}

public class EmailNotificationChannel implements NotificationChannel {
    public void send(Notification notification) {
        System.out.println("Sending Email to " + notification.getUserId() 
                          + ": " + notification.getMessage());
    }
}

// Similarly: SMSNotificationChannel, PushNotificationChannel
```

**Benefits**: ✅ Open/Closed Principle ✅ Easy to add channels ✅ Runtime flexibility

### 2. **Factory Pattern** ⭐
**Purpose**: Create channel instances based on type

**Implementation**:
```java
public class NotificationFactory {
    public static NotificationChannel getChannel(ChannelType type) {
        return switch (type) {
            case SMS -> new SMSNotificationChannel();
            case EMAIL -> new EmailNotificationChannel();
            case PUSH -> new PushNotificationChannel();
        };
    }
}
```

**Benefits**: ✅ Centralized creation ✅ Decouples client from concrete classes

### 3. **Dependency Injection**
**Purpose**: Constructor-based dependency management

**Implementation**:
```java
public class NotificationDispatcher {
    private final UserPreferenceService preferenceService;
    
    public NotificationDispatcher(UserPreferenceService service) {
        this.preferenceService = service;
    }
}
```

**Benefits**: ✅ Testable ✅ Loose coupling ✅ Configurable

### 4. **Async Pattern** ⭐
**Purpose**: Non-blocking notification processing

**Implementation**:
```java
public class AsyncNotificationService {
    private final NotificationDispatcher dispatcher;
    private final ExecutorService executorService;
    
    public AsyncNotificationService(NotificationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.executorService = Executors.newFixedThreadPool(10);
    }
    
    public void sendNotification(Notification notification) {
        executorService.submit(() -> dispatcher.dispatch(notification));
    }
}
```

**Benefits**: ✅ High throughput ✅ Non-blocking ✅ Scalable

---

## Class Diagram

See [NotificationSystem_UML.drawio](diagrams/NotificationSystem_UML.drawio)

**Key Relationships**:
- NotificationChannel ←── 3 implementations (Email, SMS, Push)
- NotificationFactory → creates NotificationChannel
- NotificationDispatcher ◆→ UserPreferenceService
- NotificationService/AsyncNotificationService ◆→ NotificationDispatcher

---

## Code Walkthrough

### Complete Notification Flow

```java
// 1. Setup preference service
UserPreferenceService preferenceService = new UserPreferenceService();

// 2. Save user preferences (Email + SMS)
preferenceService.savePreference(
    new UserPreference("user123", Set.of(ChannelType.EMAIL, ChannelType.SMS))
);

// 3. Create dispatcher
NotificationDispatcher dispatcher = new NotificationDispatcher(preferenceService);

// 4. Create async service
AsyncNotificationService asyncService = new AsyncNotificationService(dispatcher);

// 5. Send notification
Notification notification = new Notification("user123", "Order shipped!");
asyncService.sendNotification(notification);
// Output: Email sent, SMS sent (async on thread pool)
```

### Key Implementation Details

**NotificationDispatcher - Multi-channel dispatch**:
```java
public class NotificationDispatcher {
    private final UserPreferenceService preferenceService;
    
    public void dispatch(Notification notification) {
        // 1. Get user preferences
        UserPreference preference = preferenceService.getPreference(
            notification.getUserId()
        );
        Set<ChannelType> channels = preference.getPreferredChannel();
        
        // 2. Send to each preferred channel
        for (ChannelType channelType : channels) {
            NotificationChannel channel = NotificationFactory.getChannel(channelType);
            channel.send(notification);
        }
    }
}
```

**UserPreferenceService - Default fallback**:
```java
public UserPreference getPreference(String userId) {
    return preferences.getOrDefault(
        userId,
        new UserPreference(userId, Set.of(ChannelType.EMAIL))  // Default
    );
}
```

---

## Design Decisions

### 1. Why Strategy Pattern for channels?
**Problem**: Different send mechanisms (Email vs SMS vs Push)  
**Solution**: Interface with multiple implementations  
**Trade-off**: ✅ Extensible, ❌ More classes

### 2. Why Factory for channel creation?
**Problem**: Client shouldn't know concrete classes  
**Solution**: Centralized factory method  
**Alternative**: Dependency Injection container

### 3. Why separate sync and async services?
**Design**: Different use cases (critical vs bulk)  
**Benefits**: Flexibility - sync for transactional, async for bulk

### 4. Why Set<ChannelType> for preferences?
**Reason**: User can choose multiple channels  
**Implementation**: HashSet for deduplication

---

## Extensibility

### Adding New Channel (WhatsApp)
```java
// 1. Add enum
public enum ChannelType {
    SMS, EMAIL, PUSH, WHATSAPP
}

// 2. Create implementation
public class WhatsAppNotificationChannel implements NotificationChannel {
    public void send(Notification notification) {
        System.out.println("Sending WhatsApp to " + notification.getUserId());
    }
}

// 3. Update factory
public static NotificationChannel getChannel(ChannelType type) {
    return switch (type) {
        // ... existing cases
        case WHATSAPP -> new WhatsAppNotificationChannel();
    };
}
```

### Adding Priority Support
```java
public class Notification {
    private String userId, message;
    private Priority priority;  // HIGH, MEDIUM, LOW
}

public class PriorityBasedDispatcher extends NotificationDispatcher {
    public void dispatch(Notification notification) {
        if (notification.getPriority() == Priority.HIGH) {
            // Send to all channels immediately
        } else {
            // Queue for batch processing
        }
    }
}
```

### Adding Rate Limiting
```java
public class RateLimitedChannel implements NotificationChannel {
    private final NotificationChannel wrapped;
    private final RateLimiter rateLimiter;
    
    public void send(Notification notification) {
        if (rateLimiter.tryAcquire()) {
            wrapped.send(notification);
        } else {
            // Queue or retry
        }
    }
}
```

### Adding Retry Mechanism
```java
public class RetryableChannel implements NotificationChannel {
    private final NotificationChannel channel;
    private final int maxRetries = 3;
    
    public void send(Notification notification) {
        for (int i = 0; i < maxRetries; i++) {
            try {
                channel.send(notification);
                return;
            } catch (Exception e) {
                if (i == maxRetries - 1) throw e;
                Thread.sleep(1000 * (i + 1));  // Exponential backoff
            }
        }
    }
}
```

---

## Complexity Analysis

| Operation | Time | Space |
|-----------|------|-------|
| Send Notification | O(c) | O(1) |
| Get Preference | O(1) | O(1) |
| Save Preference | O(1) | O(1) |
| Dispatch | O(c) | O(1) |

*c = number of channels (typically 1-3)*

### Scalability
- **Thread Pool**: Fixed pool (10 threads) for async processing
- **Queue**: Use message queue (RabbitMQ/Kafka) for high volume
- **Batching**: Group notifications by channel for efficiency
- **Caching**: Cache user preferences (Redis)

---

## Testing Strategy

### Unit Tests
```java
@Test
void testEmailChannel() {
    NotificationChannel channel = new EmailNotificationChannel();
    Notification notification = new Notification("user123", "Test");
    
    channel.send(notification);  // Verify output
}

@Test
void testFactory() {
    NotificationChannel channel = NotificationFactory.getChannel(ChannelType.EMAIL);
    assertTrue(channel instanceof EmailNotificationChannel);
}

@Test
void testDispatcher() {
    UserPreference pref = new UserPreference("user123", 
        Set.of(ChannelType.EMAIL, ChannelType.SMS));
    preferenceService.savePreference(pref);
    
    dispatcher.dispatch(new Notification("user123", "Test"));
    // Verify 2 channels called
}
```

### Integration Test
```java
@Test
void testAsyncService() throws Exception {
    AsyncNotificationService service = new AsyncNotificationService(dispatcher);
    service.sendNotification(new Notification("user123", "Test"));
    
    Thread.sleep(100);  // Wait for async
    // Verify notification sent
}
```

### Edge Cases
- User with no preferences (defaults to Email), Empty message, Unknown channel type, Thread pool exhaustion

---

## Follow-up Questions

### Q1: How to handle notification failures?
**Answer**: Dead Letter Queue (DLQ) - on failure, push to DLQ for manual retry or investigation

### Q2: How to implement notification templates?
**Answer**: Template pattern - `NotificationTemplate` with placeholders, `TemplateEngine` to render

### Q3: How to track delivery status?
**Answer**: Event-driven - each channel publishes `NotificationSent`, `NotificationDelivered`, `NotificationFailed` events

### Q4: How to implement notification scheduling?
**Answer**: Quartz scheduler or delayed queue - schedule task to send at future time

### Q5: How to support localization?
**Answer**: `Notification` includes `locale`, service loads message from `ResourceBundle`

### Q6: How to prevent duplicate notifications?
**Answer**: Idempotency key - store `(userId, notificationId, timestamp)` in cache, check before sending

### Q7: How to implement notification grouping (digest)?
**Answer**: Batch processor - collect notifications over time window, send single digest message

### Q8: How to scale for millions of notifications/day?
**Answer**: Message queue (Kafka), multiple consumer instances, database sharding by userId, monitoring with metrics
