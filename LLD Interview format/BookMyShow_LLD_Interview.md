# BookMyShow - Low Level Design Interview

## Problem Statement

Design a **Movie Ticket Booking System** that handles browsing movies/theatres, viewing shows/seats, and booking tickets with **concurrent access** and **double booking prevention**.

**Core Features**:
- Browse movies by city/genre/language
- View shows with seat availability
- Book seats (temporary block → payment → confirmation)
- Multiple theatres, screens, seat types

---

## Requirements

### Functional Requirements
1. **Movie Management**: Add/search movies (name, genre, language)
2. **Theatre Management**: Multiple theatres → screens → seats (PREMIUM/GOLD/SILVER)
3. **Show Management**: Create shows (movie + screen + time), track seat availability
4. **Booking System**: Search shows, select seats, block (5 min timer), confirm/release
5. **Seat Management**: Prevent double booking, release expired bookings

### Non-Functional Requirements
1. **Concurrency**: Handle simultaneous bookings for same show
2. **Consistency**: No double bookings
3. **Performance**: Search < 100ms, Booking < 2s
4. **Extensibility**: Easy to add food, payments, offers

---

## Core Entities

| Entity | Description | Key Attributes |
|--------|-------------|----------------|
| **Movie** | Film shown | id, name, genre, language, duration |
| **Theatre** | Physical venue | id, name, city, screens[] |
| **Screen** | Auditorium | id, name, seats[] |
| **Seat** | Individual seat | id, seatNumber, type, price |
| **Show** | Movie screening | id, movie, screen, startTime, bookedSeats[] |
| **Booking** | User reservation | id, show, seats[], user, status |

---

## Design Patterns

### 1. **Singleton Pattern** ⭐
**Purpose**: Single instance of BookingService for centralized booking management

**Implementation**:
```java
public class BookingService {
    private static BookingService instance;
    private final Map<String, Booking> bookings;
    
    private BookingService() {
        bookings = new ConcurrentHashMap<>();
    }
    
    public static synchronized BookingService getInstance() {
        if (instance == null) {
            instance = new BookingService();
        }
        return instance;
    }
}
```

**Benefits**: ✅ Centralized control ✅ Thread-safe with ConcurrentHashMap

### 2. **Factory Pattern** ⭐
**Purpose**: Create entities with validation

**Implementation**:
```java
public class MovieFactory {
    public static Movie createMovie(String name, Genre genre, 
                                     Language language, int duration) {
        if (duration <= 0) throw new IllegalArgumentException();
        return new Movie(UUID.randomUUID().toString(), 
                        name, genre, language, duration);
    }
}
```

**Benefits**: ✅ Centralized validation ✅ Consistent creation

### 3. **MVC Pattern** ⭐
**Purpose**: Separate data, logic, presentation

**Structure**: Models → Controllers → Service (Singleton)

```java
public class BookingController {
    private final BookingService service = BookingService.getInstance();
    
    public Booking createBooking(Show show, List<Seat> seats) {
        return service.createBooking(show, seats, userId);
    }
}
```

### 4. **Repository Pattern**
**Purpose**: Abstract data persistence

```java
public class ShowRepository {
    private Map<String, Show> shows = new HashMap<>();
    public void save(Show show) { shows.put(show.getId(), show); }
    public List<Show> findByMovieAndCity(Movie m, String city) { /*...*/ }
}
```

---

## Class Diagram

See [BookMyShow_UML.drawio](diagrams/BookMyShow_UML.drawio)

**Key Relationships**:
- Theatre ◆→ Screen (Composition 1:N)
- Screen ◆→ Seat (Composition 1:N)
- Show ○→ Movie, Screen (Association N:1)
- Booking ○→ Show, Seat[] (Association N:1, N:M)

---

## Code Walkthrough

### Complete Booking Flow

```java
// 1. Create entities
Movie movie = MovieFactory.createMovie("Inception", Genre.SCI_FI, 
                                       Language.ENGLISH, 148);
Theatre theatre = TheatreFactory.createTheatre("PVR Phoenix", 
                                               "Mumbai", 5, 100);

// 2. Create show
Show show = new Show(UUID.randomUUID().toString(), movie,
                     theatre.getScreens().get(0),
                     LocalDateTime.now().plusHours(2));

// 3. Book seats (5-minute timer starts)
BookingController controller = new BookingController();
List<Seat> selectedSeats = List.of(screen.getSeats().get(0), 
                                    screen.getSeats().get(1));
Booking booking = controller.createBooking(show, selectedSeats, "user123");
// Status: PENDING, seats blocked in Show

// 4. Confirm booking (after payment)
controller.confirmBooking(booking.getId());
// Status: CONFIRMED
```

### Key Implementation Details

**Show - Thread-safe seat booking**:
```java
public class Show {
    private final List<String> bookedSeatIds;  // CopyOnWriteArrayList
    
    public synchronized boolean bookSeats(List<String> seatIds) {
        // Check availability
        if (bookedSeatIds.containsAny(seatIds)) return false;
        
        // Book seats
        bookedSeatIds.addAll(seatIds);
        return true;
    }
}
```

**BookingService - Auto-cancel pending bookings**:
```java
public Booking createBooking(Show show, List<Seat> seats, String userId) {
    Booking booking = new Booking(/*...*/);
    booking.setStatus(BookingStatus.PENDING);
    show.bookSeats(seats);
    
    // Auto-cancel after 5 minutes
    executor.schedule(() -> {
        if (booking.getStatus() == BookingStatus.PENDING) {
            cancelBooking(booking.getId());
        }
    }, 5, TimeUnit.MINUTES);
    
    return booking;
}
```

---

## Design Decisions

### 1. Why Singleton for BookingService?
**Problem**: Multiple instances → inconsistent state  
**Solution**: Single instance for centralized control  
**Trade-off**: ✅ Consistent state, ❌ Hard to test  
**Production**: Use Dependency Injection (@Singleton)

### 2. Why synchronize Show.bookSeats()?
**Problem**: Race condition for same seat  
**Solution**: Synchronized atomic check-and-update  
**Trade-off**: ✅ Thread-safe, ❌ Bottleneck at high concurrency  
**Alternatives**: Optimistic locking, Redis distributed lock

### 3. Why 5-minute timeout?
**Business**: Balance UX vs seat availability  
**Implementation**: ScheduledExecutorService  
**Alternative**: Quartz scheduler, Database TTL

### 4. Why separate Screen and Show?
**Design**: Screen = physical, Show = temporal event  
**Benefits**: Same screen → multiple shows, easy querying

---

## Extensibility

### Adding Food Booking
```java
public class FoodItem {
    private final String id, name;
    private final double price;
}

public class Booking {
    private final List<FoodItem> foodItems;  // Add field
}
```

### Dynamic Pricing
```java
public interface PricingStrategy {
    double calculatePrice(Seat seat, Show show);
}

public class WeekendPricing implements PricingStrategy {
    public double calculatePrice(Seat seat, Show show) {
        return seat.getPrice() * 1.2;  // 20% markup
    }
}
```

### Adding Offers/Coupons
```java
public interface Discount {
    double apply(double originalPrice);
}

public class PercentageDiscount implements Discount {
    public double apply(double price) {
        return price * (1 - percentage / 100);
    }
}
```

### Notifications
```java
public interface NotificationService {
    void sendBookingConfirmation(Booking booking);
}

public class EmailNotificationService implements NotificationService {
    public void sendBookingConfirmation(Booking booking) {
        // Send email with booking details
    }
}
```

---

## Complexity Analysis

| Operation | Time | Space |
|-----------|------|-------|
| Create Booking | O(s) | O(1) |
| Confirm Booking | O(1) | O(1) |
| Search Shows | O(n) | O(1) |
| Get Available Seats | O(s) | O(1) |
| Cancel Booking | O(s) | O(1) |

*s = seats, n = shows*

### Scalability
- **Database Indexing**: Index on movie_id, city, start_time
- **Caching**: Cache popular shows (10 min TTL)
- **Sharding**: Shard by city
- **Read Replicas**: Use replicas for searches

---

## Testing Strategy

### Unit Tests
```java
@Test
void testCreateBooking() {
    Booking booking = service.createBooking(show, seats, "user123");
    assertEquals(BookingStatus.PENDING, booking.getStatus());
}

@Test
void testBookSeats_alreadyBooked() {
    show.bookSeats(List.of("A1"));
    boolean booked = show.bookSeats(List.of("A1"));
    assertFalse(booked);  // Prevent double booking
}

@Test
void testConcurrentBooking() throws Exception {
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<Boolean> f1 = executor.submit(() -> show.bookSeats(List.of("A1")));
    Future<Boolean> f2 = executor.submit(() -> show.bookSeats(List.of("A1")));
    
    assertTrue(f1.get() ^ f2.get());  // Exactly one succeeds
}
```

### Integration Test
```java
@Test
void testCompleteFlow() {
    // Create → Confirm → Verify
    Booking booking = controller.createBooking(show, seats, "user123");
    controller.confirmBooking(booking.getId());
    
    assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    assertTrue(show.getBookedSeatIds().contains(seatId));
}
```

### Edge Cases
- Booking already booked seats, Timeout cancellation, Concurrent access, Null inputs

---

## Follow-up Questions

### Q1: How to prevent double booking at scale?
**Answer**: Database-level locking (Optimistic with @Version, Pessimistic with FOR UPDATE, or Redis distributed lock)

### Q2: How to handle payment failures?
**Answer**: Compensating transaction - cancel booking and release seats. Use Saga pattern for complex flows.

### Q3: How to implement dynamic pricing?
**Answer**: Strategy pattern - `SurgePricing` (closer to show = higher price), `WeekendPricing`, `HolidayPricing`

### Q4: How to scale for millions of users?
**Answer**: Caching (Redis), DB sharding by city, read replicas for searches, message queue for notifications, CDN for static content, microservices

### Q5: How to implement waitlist?
**Answer**: Queue-based system - on cancellation, notify first matching waitlist entry

### Q6: How to support recurring shows?
**Answer**: Template pattern - store template (movie, screen, time, days[]), generate shows for date range

### Q7: How to implement seat recommendations?
**Answer**: Algorithm - find contiguous seats in same row, prioritize center seats

### Q8: How to handle show cancellations?
**Answer**: Event-driven - find all bookings, initiate refunds (message queue), notify users
