# Vending Machine - Low Level Design Interview

## Problem Statement

Design a **Vending Machine System** that manages inventory, accepts coins, validates payment, dispenses products, and returns change with **state-based flow control**.

**Core Features**:
- State management (Idle, HasMoney, Selection, Dispense, OutOfStock)
- Coin insertion and balance tracking
- Product selection with price validation
- Inventory management with shelf-based storage
- Change calculation and return

---

## Requirements

### Functional Requirements
1. **Coin Management**: Accept coins (1, 2, 5, 10 rupees), track balance
2. **Inventory Management**: 10 shelves, each holds multiple items, track sold-out status
3. **Product Selection**: Select by code, validate sufficient balance
4. **Dispensing**: Dispense product, remove from inventory, return change
5. **State Transitions**: Idle → HasMoney → Selection → Dispense → Idle

### Non-Functional Requirements
1. **State Consistency**: Clean state transitions, no invalid operations
2. **Inventory Tracking**: Per-shelf stock management
3. **Extensibility**: Easy to add new states or product types
4. **Error Handling**: Handle insufficient funds, out-of-stock, invalid codes

---

## Core Entities

| Entity | Description | Key Attributes |
|--------|-------------|----------------|
| **VendingMachineContext** | State machine context | currentState, inventory, coinList, selectedItemCode |
| **VendingMachineState** | State interface | getStateName(), next() |
| **Inventory** | Manages shelves | inventory: ItemShelf[], hasItems() |
| **ItemShelf** | Storage slot | code, items[], isSoldOut |
| **Item** | Product | type, price |
| **Coin** | Currency enum | ONE_RUPEE(1), TWO_RUPEE(2), FIVE_RUPEE(5), TEN_RUPEE(10) |
| **ItemType** | Product enum | COKE, PEPSI, JUICE, SODA |

---

## Design Patterns

### 1. **State Pattern** ⭐
**Purpose**: Manage vending machine states and transitions without complex conditionals

**States**:
- **IdleState**: Waiting for coin insertion
- **HasMoneyState**: Coins inserted, waiting for selection button
- **SelectionState**: User selecting product
- **DispenseState**: Dispensing product
- **OutOfStockState**: No items available

**Implementation**:
```java
public interface VendingMachineState {
    String getStateName();
    VendingMachineState next(VendingMachineContext context);
}

public class IdleState implements VendingMachineState {
    public VendingMachineState next(VendingMachineContext context) {
        if (!context.getInventory().hasItems()) {
            return new OutOfStockState();
        }
        if (!context.getCoinList().isEmpty()) {
            return new HasMoneyState();
        }
        return this;
    }
}

public class HasMoneyState implements VendingMachineState {
    public VendingMachineState next(VendingMachineContext context) {
        if (context.getCurrentState() instanceof HasMoneyState) {
            return new SelectionState();
        }
        return this;
    }
}

public class SelectionState implements VendingMachineState {
    public VendingMachineState next(VendingMachineContext context) {
        if (context.getSelectedItemCode() > 0) {
            return new DispenseState();
        }
        return this;
    }
}

public class DispenseState implements VendingMachineState {
    public VendingMachineState next(VendingMachineContext context) {
        return new IdleState();  // After dispensing, go back to idle
    }
}
```

**Benefits**: ✅ Clean transitions ✅ No complex if-else ✅ Easy to add states

### 2. **Context Pattern**
**Purpose**: Centralize state management and operations

**Implementation**:
```java
public class VendingMachineContext {
    private VendingMachineState currentState;
    private Inventory inventory;
    private int selectedItemCode;
    private List<Coin> coinList;
    
    public VendingMachineContext() {
        inventory = new Inventory(10);
        coinList = new ArrayList<>();
        currentState = new IdleState();
    }
    
    public void advanceState() {
        VendingMachineState nextState = currentState.next(this);
        currentState = nextState;
        System.out.println("Current state: " + currentState.getStateName());
    }
    
    public void clickOnInsertCoinButton(Coin coin) {
        if (currentState instanceof IdleState || currentState instanceof HasMoneyState) {
            System.out.println("Inserted " + coin.name() + " worth " + coin.value);
            coinList.add(coin);
            advanceState();
        } else {
            System.out.println("Cannot insert coin in " + currentState.getStateName());
        }
    }
    
    public int getBalance() {
        return coinList.stream().mapToInt(coin -> coin.value).sum();
    }
}
```

**Benefits**: ✅ Single point of control ✅ State validation ✅ Encapsulation

---

## Class Diagram

See [VendingMachine_UML.drawio](diagrams/VendingMachine_UML.drawio)

**Key Relationships**:
- VendingMachineContext ◆→ VendingMachineState (has current state)
- VendingMachineContext ◆→ Inventory
- VendingMachineState ←── IdleState, HasMoneyState, SelectionState, DispenseState, OutOfStockState
- Inventory → manages → ItemShelf[]
- ItemShelf → contains → List<Item>

---

## Code Walkthrough

### Complete Purchase Flow

```java
// 1. Initialize vending machine
VendingMachineContext vendingMachine = new VendingMachineContext();
// State: IdleState

// 2. Fill inventory
Item coke = new Item();
coke.setType(ItemType.COKE);
coke.setPrice(12);
vendingMachine.updateInventory(coke, 101);  // Add to shelf 101

// 3. Insert coins
vendingMachine.clickOnInsertCoinButton(Coin.TEN_RUPEE);
// State: IdleState → HasMoneyState
// Balance: 10

vendingMachine.clickOnInsertCoinButton(Coin.FIVE_RUPEE);
// State: remains HasMoneyState
// Balance: 15

// 4. Select product
vendingMachine.clickOnStartProductSelectionButton(101);
// State: HasMoneyState → SelectionState
// Validates: balance (15) >= price (12) ✓
// State: SelectionState → DispenseState

// 5. Dispense
// - Removes item from inventory
// - Calculates change: 15 - 12 = 3
// - Returns change
// - Resets balance and selection
// State: DispenseState → IdleState
```

### Key Implementation Details

**Inventory - Shelf-based management**:
```java
public class Inventory {
    ItemShelf[] inventory;
    
    public Inventory(int itemCount) {
        inventory = new ItemShelf[itemCount];
        initializeInventory();  // Creates shelves 101-110
    }
    
    public Item getItem(int codeNumber) throws Exception {
        for (ItemShelf shelf : inventory) {
            if (shelf.getCode() == codeNumber) {
                if (shelf.checkIsSoldOut()) {
                    throw new Exception("Item already sold out");
                }
                return shelf.getItems().get(0);
            }
        }
        throw new Exception("Invalid Code");
    }
    
    public void removeItem(int codeNumber) throws Exception {
        for (ItemShelf shelf : inventory) {
            if (shelf.getCode() == codeNumber) {
                shelf.getItems().remove(0);
                if (shelf.getItems().isEmpty()) {
                    shelf.setIsSoldOut(true);
                }
            }
        }
    }
}
```

**Selection and Dispensing**:
```java
public void selectProduct(int codeNumber) {
    if (currentState instanceof SelectionState) {
        try {
            Item item = inventory.getItem(codeNumber);
            int balance = getBalance();
            
            if (balance < item.getPrice()) {
                System.out.println("Insufficient amount. Price: " + item.getPrice() 
                                  + ", paid: " + balance);
                return;
            }
            
            setSelectedItemCode(codeNumber);
            advanceState();  // → DispenseState
            dispenseItem(codeNumber);
            
            int change = balance - item.getPrice();
            System.out.println("Returning change: " + change);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

public void dispenseItem(int codeNumber) {
    if (currentState instanceof DispenseState) {
        try {
            Item item = inventory.getItem(codeNumber);
            System.out.println("Dispensing " + item.getType());
            
            inventory.removeItem(codeNumber);
            inventory.updateSoldItem(codeNumber);
            resetBalance();
            resetSelection();
            advanceState();  // → IdleState
        } catch (Exception e) {
            System.out.println("Failed to dispense: " + e.getMessage());
        }
    }
}
```

---

## Design Decisions

### 1. Why State Pattern?
**Problem**: Complex state management with different allowed operations per state  
**Solution**: Each state = separate class with specific transitions  
**Trade-off**: ✅ Clean, maintainable, ❌ More classes

### 2. Why separate Context class?
**Design**: Centralize state transitions and shared data  
**Benefits**: Single point of control, encapsulation, state validation

### 3. Why shelf-based inventory?
**Design**: Each shelf has code (101-110) and can hold multiple same items  
**Benefits**: Real-world mapping, batch refilling, per-shelf sold-out tracking

### 4. Why List<Coin> instead of single balance?
**Reason**: Track actual coins for change calculation  
**Alternative**: Single int balance (simpler but less realistic)

---

## Extensibility

### Adding Return Coin Button
```java
public void clickOnReturnCoinButton() {
    if (currentState instanceof HasMoneyState || currentState instanceof SelectionState) {
        int balance = getBalance();
        System.out.println("Returning " + balance + " rupees");
        resetBalance();
        currentState = new IdleState();
    }
}
```

### Adding Refund State
```java
public class RefundState implements VendingMachineState {
    public VendingMachineState next(VendingMachineContext context) {
        context.resetBalance();
        return new IdleState();
    }
}
```

### Supporting Notes (Bills)
```java
public enum Note {
    TEN_RUPEE(10),
    TWENTY_RUPEE(20),
    FIFTY_RUPEE(50);
    
    public int value;
}

public class VendingMachineContext {
    private List<Note> noteList;
    
    public void clickOnInsertNoteButton(Note note) {
        noteList.add(note);
        advanceState();
    }
}
```

### Adding Payment Validation State
```java
public class PaymentValidationState implements VendingMachineState {
    public VendingMachineState next(VendingMachineContext context) {
        int balance = context.getBalance();
        int price = context.getInventory()
                          .getItem(context.getSelectedItemCode())
                          .getPrice();
        
        if (balance >= price) {
            return new DispenseState();
        } else {
            return new HasMoneyState();  // Need more money
        }
    }
}
```

---

## Complexity Analysis

| Operation | Time | Space |
|-----------|------|-------|
| Insert Coin | O(1) | O(1) |
| State Transition | O(1) | O(1) |
| Select Product | O(n) | O(1) |
| Get Balance | O(c) | O(1) |
| Dispense | O(n) | O(1) |

*n = number of shelves (10), c = number of coins inserted*

### Space
- **Inventory**: O(n × m) where n = shelves, m = items per shelf
- **Coin List**: O(c) where c = coins inserted
- **States**: O(1) constant

---

## Testing Strategy

### Unit Tests
```java
@Test
void testStateTransitions() {
    VendingMachineContext vm = new VendingMachineContext();
    assertEquals("IdleState", vm.getCurrentState().getStateName());
    
    vm.clickOnInsertCoinButton(Coin.TEN_RUPEE);
    assertEquals("HasMoneyState", vm.getCurrentState().getStateName());
}

@Test
void testInsufficientFunds() {
    VendingMachineContext vm = new VendingMachineContext();
    Item item = new Item();
    item.setType(ItemType.COKE);
    item.setPrice(12);
    vm.updateInventory(item, 101);
    
    vm.clickOnInsertCoinButton(Coin.FIVE_RUPEE);  // Only 5 rupees
    vm.clickOnStartProductSelectionButton(101);
    
    // Should not dispense - insufficient funds
    assertEquals(5, vm.getBalance());
}

@Test
void testChangeCalculation() {
    VendingMachineContext vm = new VendingMachineContext();
    Item item = new Item();
    item.setPrice(12);
    vm.updateInventory(item, 101);
    
    vm.clickOnInsertCoinButton(Coin.TEN_RUPEE);
    vm.clickOnInsertCoinButton(Coin.FIVE_RUPEE);  // Total: 15
    
    vm.clickOnStartProductSelectionButton(101);
    // Expected change: 15 - 12 = 3
}
```

### Integration Test
```java
@Test
void testCompletePurchaseFlow() {
    VendingMachineContext vm = new VendingMachineContext();
    
    // Setup
    Item coke = new Item();
    coke.setType(ItemType.COKE);
    coke.setPrice(12);
    vm.updateInventory(coke, 101);
    
    // Insert coins
    vm.clickOnInsertCoinButton(Coin.TEN_RUPEE);
    vm.clickOnInsertCoinButton(Coin.FIVE_RUPEE);
    
    // Purchase
    vm.clickOnStartProductSelectionButton(101);
    
    // Verify
    assertEquals("IdleState", vm.getCurrentState().getStateName());
    assertEquals(0, vm.getBalance());
}
```

### Edge Cases
- Out of stock, Invalid shelf code, Exact change, No change needed, Multiple purchases

---

## Follow-up Questions

### Q1: How to handle exact change shortage?
**Answer**: Track available change inventory. If cannot provide exact change, reject transaction or accept "keep the change" mode.

### Q2: How to implement multi-product selection?
**Answer**: Add cart state - allow multiple selections before payment, aggregate prices, single dispense phase.

### Q3: How to handle concurrent access?
**Answer**: Add locking mechanism (synchronized methods) or use state machine with atomic transitions (AtomicReference for currentState).

### Q4: How to add card payment?
**Answer**: New state `CardPaymentState`, integrate payment gateway API, add timeout for payment processing.

### Q5: How to implement restocking?
**Answer**: Admin mode - special state that allows inventory updates, requires authentication, bulk add items per shelf.

### Q6: How to track sales analytics?
**Answer**: Observer pattern - notify listeners on sales events, log transactions (timestamp, item, amount, change), aggregate stats.

### Q7: How to handle expired products?
**Answer**: Add `expiryDate` to Item, check on selection, auto-remove expired items via scheduled task.

### Q8: How to optimize for high-traffic locations?
**Answer**: Pre-calculate combinations for common prices, cache inventory status, use event-driven architecture for async operations, consider queueing system for multiple users
