# Elevator System - Low Level Design Interview

## Problem Statement

Design an **Elevator Control System** that manages multiple elevators, handles floor requests efficiently, and maintains elevator state transitions with support for **different selection strategies**.

**Core Features**:
- Multiple elevators with independent queues
- Floor request panels (UP/DOWN buttons)
- Elevator selection (nearest, load balancing)
- State management (Idle, MoveUp, MoveDown)
- Observer notifications for panel updates

---

## Requirements

### Functional Requirements
1. **Elevator Management**: Multiple elevators, each with queue and current floor
2. **Floor Requests**: Outer panels request elevator with direction (UP/DOWN)
3. **Elevator Selection**: Strategy-based selection (nearest elevator, load balancing)
4. **State Transitions**: Idle → MoveUp/MoveDown → Idle
5. **Notifications**: Panels notified when elevator arrives at their floor

### Non-Functional Requirements
1. **Extensibility**: Easy to add new selection strategies
2. **Flexibility**: Runtime strategy switching
3. **Scalability**: Support many elevators and floors
4. **Maintainability**: Clean state management with State pattern

---

## Core Entities

| Entity | Description | Key Attributes |
|--------|-------------|----------------|
| **Elevator** | Individual elevator | manager, state, currentFloor, queue |
| **ElevatorManager** | Manages all elevators | elevators[], strategy, observers[], panels[] |
| **State** | Elevator state interface | moveUp(), moveDown(), stop() |
| **ElevatorSelectionStrategy** | Selection strategy | select(elevators, floor, direction) |
| **OuterPanel** | Floor request panel | floor, manager (Observer) |
| **Direction** | Request direction | UP, DOWN |

---

## Design Patterns

### 1. **State Pattern** ⭐
**Purpose**: Manage elevator state transitions (Idle, MoveUp, MoveDown)

**Implementation**:
```java
public interface State {
    void moveUp();
    void moveDown();
    void stop();
}

public class IdleState implements State {
    private Elevator elevator;
    
    public void moveUp() {
        elevator.setState(new MoveUpState(elevator));
    }
    
    public void stop() {
        System.out.println("Elevator idle at floor " + elevator.getCurrentFloor());
    }
}

public class MoveUpState implements State {
    public void moveUp() {
        int nextFloor = elevator.getCurrentFloor() + 1;
        elevator.setCurrentFloor(nextFloor);
        System.out.println("Moving UP -> floor " + nextFloor);
    }
    
    public void stop() {
        System.out.println("Stopped at floor " + elevator.getCurrentFloor());
        elevator.setState(new IdleState(elevator));
    }
}

// Similarly: MoveDownState
```

**Benefits**: ✅ Clean transitions ✅ No complex if-else ✅ Easy to add states

### 2. **Strategy Pattern** ⭐
**Purpose**: Different elevator selection algorithms

**Implementation**:
```java
public interface ElevatorSelectionStrategy {
    Elevator select(List<Elevator> elevators, int floor, Direction direction);
}

// Strategy 1: Nearest Elevator
public class NearestElevatorStrategy implements ElevatorSelectionStrategy {
    public Elevator select(List<Elevator> elevators, int floor, Direction dir) {
        int min = Integer.MAX_VALUE;
        Elevator best = null;
        
        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.getCurrentFloor() - floor);
            if (distance < min) {
                min = distance;
                best = elevator;
            }
        }
        return best;
    }
}

// Strategy 2: Load Balancing
public class LoadBalancingSelectionStrategy implements ElevatorSelectionStrategy {
    private Random random = new Random();
    
    public Elevator select(List<Elevator> elevators, int floor, Direction dir) {
        return elevators.get(random.nextInt(elevators.size()));
    }
}
```

**Benefits**: ✅ Runtime flexibility ✅ Easy to add strategies ✅ Open/Closed Principle

### 3. **Observer Pattern** ⭐
**Purpose**: Notify floor panels when elevator moves

**Implementation**:
```java
public interface ElevatorObserver {
    void update(Elevator elevator);
}

public class OuterPanel implements ElevatorObserver {
    private ElevatorManager manager;
    private int floor;
    
    public void update(Elevator elevator) {
        System.out.println("Panel floor " + floor + 
                          " notified: elevator at " + elevator.getCurrentFloor());
    }
    
    public void requestElevator(Direction direction) {
        manager.addToQueue(floor, direction);
    }
}

public class ElevatorManager {
    private List<ElevatorObserver> observers;
    
    public void notifyObservers(Elevator elevator) {
        for (ElevatorObserver obs : observers) {
            obs.update(elevator);
        }
    }
}
```

**Benefits**: ✅ Decoupled communication ✅ Multiple observers ✅ Extensible

---

## Class Diagram

See [ElevatorSystem_UML.drawio](diagrams/ElevatorSystem_UML.drawio)

**Key Relationships**:
- Elevator ◆→ State (has state)
- State ←── IdleState, MoveUpState, MoveDownState (implementations)
- ElevatorManager ◆→ ElevatorSelectionStrategy
- ElevatorSelectionStrategy ←── NearestElevatorStrategy, LoadBalancingSelectionStrategy
- ElevatorManager → notifies → ElevatorObserver
- OuterPanel implements ElevatorObserver

---

## Code Walkthrough

### Complete Request Flow

```java
// 1. Setup system with nearest elevator strategy
ElevatorManager manager = new ElevatorManager(new NearestElevatorStrategy());

// 2. Add elevators at different floors
Elevator e1 = new Elevator(0, manager);  // Ground floor
Elevator e2 = new Elevator(5, manager);  // 5th floor
manager.addElevator(e1);
manager.addElevator(e2);

// 3. Add floor panels (observers)
OuterPanel panel3 = new OuterPanel(manager, 3);
OuterPanel panel7 = new OuterPanel(manager, 7);
manager.addPanels(panel3);  // Registers as observer
manager.addPanels(panel7);

// 4. User at floor 3 requests elevator going UP
panel3.requestElevator(Direction.UP);
// Output:
// - Manager uses NearestElevatorStrategy
// - Selects e1 (distance: 3) over e2 (distance: 2) → e2 selected
// - e2 moves: 5 → 4 → 3 (state: MoveDown)
// - Stops at floor 3 (state: Idle)
// - All panels notified: "Panel floor 3 notified: elevator at 3"
```

### Key Implementation Details

**Elevator - Queue processing**:
```java
public class Elevator {
    private State state;
    private int currentFloor;
    private Queue<Integer> queue;
    
    public void processQueue() {
        while (!queue.isEmpty()) {
            int target = queue.poll();
            
            // Set state based on direction
            if (target > currentFloor) {
                setState(new MoveUpState(this));
            } else if (target < currentFloor) {
                setState(new MoveDownState(this));
            }
            
            // Move to target floor
            while (currentFloor != target) {
                if (target > currentFloor) state.moveUp();
                else state.moveDown();
            }
            
            // Stop and notify
            state.stop();
            manager.notifyObservers(this);
        }
    }
}
```

**ElevatorManager - Strategy-based selection**:
```java
public void addToQueue(int floor, Direction direction) {
    // Use strategy to select best elevator
    Elevator elevator = strategy.select(elevators, floor, direction);
    System.out.println("Selected elevator at floor " + elevator.getCurrentFloor());
    
    elevator.addToQueue(floor);
    elevator.processQueue();
}
```

---

## Design Decisions

### 1. Why State Pattern?
**Problem**: Complex state transitions with different behaviors  
**Solution**: Each state = separate class with specific behavior  
**Trade-off**: ✅ Maintainable, ❌ More classes

### 2. Why Strategy Pattern for selection?
**Problem**: Different selection algorithms (nearest, load balancing, others)  
**Solution**: Pluggable strategy interface  
**Benefits**: Runtime flexibility, easy to add algorithms

### 3. Why Observer Pattern?
**Problem**: Panels need updates when elevator moves  
**Solution**: Panels implement Observer, Manager notifies  
**Alternative**: Polling (inefficient)

### 4. Why Queue in Elevator?
**Design**: Single elevator can serve multiple floors sequentially  
**Benefits**: FIFO ordering, batch processing

---

## Extensibility

### Adding Zone-Based Strategy
```java
public class ZoneBasedStrategy implements ElevatorSelectionStrategy {
    public Elevator select(List<Elevator> elevators, int floor, Direction dir) {
        // Assign elevators to zones: 0-10, 11-20, etc.
        int zone = floor / 10;
        return elevators.stream()
            .filter(e -> e.getCurrentFloor() / 10 == zone)
            .findFirst()
            .orElse(elevators.get(0));
    }
}
```

### Adding Priority Requests
```java
public class PriorityRequest {
    private int floor;
    private Direction direction;
    private int priority;  // 1=HIGH, 2=MEDIUM, 3=LOW
}

public class Elevator {
    private PriorityQueue<PriorityRequest> priorityQueue;  // Instead of Queue
}
```

### Adding Express Elevators
```java
public class ExpressElevator extends Elevator {
    private Set<Integer> expressFloors;  // Only stops at express floors
    
    public void addToQueue(int floor) {
        if (expressFloors.contains(floor)) {
            super.addToQueue(floor);
        }
    }
}
```

### Adding Capacity Limits
```java
public class Elevator {
    private int capacity = 10;
    private int currentLoad = 0;
    
    public boolean canAcceptRequest() {
        return currentLoad < capacity;
    }
}

public class CapacityAwareStrategy implements ElevatorSelectionStrategy {
    public Elevator select(List<Elevator> elevators, int floor, Direction dir) {
        return elevators.stream()
            .filter(Elevator::canAcceptRequest)
            .min(Comparator.comparingInt(e -> Math.abs(e.getCurrentFloor() - floor)))
            .orElse(null);
    }
}
```

---

## Complexity Analysis

| Operation | Time | Space |
|-----------|------|-------|
| Request Elevator | O(e) | O(1) |
| Select Elevator (Nearest) | O(e) | O(1) |
| Select Elevator (LoadBalance) | O(1) | O(1) |
| Process Queue | O(f × q) | O(q) |
| Notify Observers | O(o) | O(1) |

*e = elevators, f = floors, q = queue size, o = observers*

### Scalability
- **Multiple elevators**: Linear scan for selection (can optimize with indexing)
- **Large buildings**: Consider zone-based strategies
- **High traffic**: Use priority queues, smart scheduling algorithms

---

## Testing Strategy

### Unit Tests
```java
@Test
void testStateTransitions() {
    Elevator elevator = new Elevator(0, manager);
    assertEquals(IdleState.class, elevator.getState().getClass());
    
    elevator.setState(new MoveUpState(elevator));
    elevator.getState().moveUp();
    assertEquals(1, elevator.getCurrentFloor());
}

@Test
void testNearestElevatorStrategy() {
    NearestElevatorStrategy strategy = new NearestElevatorStrategy();
    Elevator e1 = new Elevator(0, manager);
    Elevator e2 = new Elevator(10, manager);
    
    Elevator selected = strategy.select(List.of(e1, e2), 3, Direction.UP);
    assertEquals(e1, selected);  // Closer to floor 3
}

@Test
void testObserverNotification() {
    OuterPanel panel = new OuterPanel(manager, 5);
    manager.addPanels(panel);
    
    Elevator elevator = new Elevator(5, manager);
    manager.notifyObservers(elevator);
    // Verify panel.update() was called
}
```

### Integration Test
```java
@Test
void testCompleteFlow() {
    ElevatorManager manager = new ElevatorManager(new NearestElevatorStrategy());
    Elevator e1 = new Elevator(0, manager);
    manager.addElevator(e1);
    
    OuterPanel panel = new OuterPanel(manager, 5);
    manager.addPanels(panel);
    
    panel.requestElevator(Direction.UP);
    
    assertEquals(5, e1.getCurrentFloor());
    assertEquals(IdleState.class, e1.getState().getClass());
}
```

### Edge Cases
- Empty elevator list, Floor out of bounds, Multiple simultaneous requests, Queue overflow

---

## Follow-up Questions

### Q1: How to handle multiple simultaneous requests?
**Answer**: Queue all requests, process FIFO. For optimization, use direction-aware scheduling (SCAN algorithm - continue in same direction).

### Q2: How to optimize for peak hours?
**Answer**: Adaptive strategy - switch to LoadBalancing during peak, ZoneBased for efficiency, or predict traffic patterns with ML.

### Q3: How to handle elevator breakdowns?
**Answer**: Health check service, mark elevator as OUT_OF_SERVICE, redistribute requests to active elevators.

### Q4: How to implement express elevators?
**Answer**: Subclass with `expressFloors: Set<Integer>`, override `addToQueue()` to validate floor, separate strategy for express.

### Q5: How to support destination dispatch?
**Answer**: User enters destination floor at panel, system assigns specific elevator, more efficient batching.

### Q6: How to add weight/capacity constraints?
**Answer**: `Elevator` has `capacity` and `currentLoad`, selection strategy filters by `canAcceptRequest()`.

### Q7: How to implement SCAN scheduling algorithm?
**Answer**: Elevator continues in same direction until no more requests, then reverses. Modify `processQueue()` to sort by direction.

### Q8: How to scale for 100+ floor buildings?
**Answer**: Zone-based elevators (low/mid/high rise), express elevators skip floors, double-deck elevators, sophisticated dispatch algorithms
