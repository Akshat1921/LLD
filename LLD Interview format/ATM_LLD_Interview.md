# ATM Machine - Low Level Design Interview

## Problem Statement

Design an **ATM System** that handles card insertion, PIN authentication, cash withdrawal in multiple denominations, and balance tracking.

**Core Features**:
- Insert card → Authenticate PIN → Select withdrawal → Dispense cash
- Multiple ATMs with varying cash inventory
- Support ₹2000, ₹500, ₹100 denominations

---

## Requirements

### Functional Requirements
1. **Card Operations**: Insert/eject card
2. **Authentication**: Validate PIN
3. **Cash Withdrawal**: Dispense optimal denominations, update balances
4. **State Management**: Track ATM state (IDLE → CARD_INSERTED → AUTHENTICATED → DISPENSE_CASH)
5. **Validation**: Check ATM cash, account balance, denomination availability

### Non-Functional Requirements
1. **Extensibility**: Easy to add states/denominations
2. **Performance**: O(1) ATM lookup, O(d) dispensing (d = denominations)
3. **Reliability**: Handle errors gracefully

---

## Core Entities

| Entity | Description | Key Attributes |
|--------|-------------|----------------|
| **ATM** | Physical machine | id, status, cashAvailable, denominationCounts |
| **Card** | User's card | cardNumber, pin, account |
| **Account** | Bank account | accountNumber, balance |
| **ATMState** | State behavior | Interface with 4 implementations |
| **CashDispenser** | Denomination handler | Chain of 3 dispensers |
| **ATMMachine** | Orchestrator | Current state, ATM, card |

---

## Design Patterns

### 1. **State Pattern** ⭐
**Purpose**: Manage ATM state transitions without complex if-else

**States**: `IdleState` → `CardInsertedState` → `AuthenticatedState` → `DispenseCashState`

**Implementation**:
```java
public interface ATMState {
    void insertCard(Card card);
    void enterPin(String pin);
    void selectOption(String option);
    void dispenseCash(int amount);
    void ejectCard();
}

// IdleState - Only accepts card insertion
public class IdleState implements ATMState {
    public void insertCard(Card card) {
        atmMachine.setCurrentCard(card);
        atmMachine.setState(new CardInsertedState(atmMachine));
    }
    // Other methods: print "No card inserted"
}

// DispenseCashState - Validates and dispenses
public class DispenseCashState implements ATMState {
    public void dispenseCash(int amount) {
        // 1. Check ATM has cash
        // 2. Check account has balance
        // 3. Check denomination availability
        // 4. Dispense using chain
        // 5. Update balances
        // 6. Eject card
    }
}
```

**Benefits**: ✅ Clean transitions ✅ Easy to add states ✅ No complex conditionals

### 2. **Chain of Responsibility Pattern** ⭐
**Purpose**: Dispense cash using greedy algorithm with fallback

**Chain**: `₹2000 → ₹500 → ₹100`

**Implementation**:
```java
public interface CashDispenser {
    void setNextDispenser(CashDispenser next);
    boolean canDispense(ATM atm, int amount);
    void dispense(ATM atm, int amount);
}

public class TwoThousandDispenser implements CashDispenser {
    private CashDispenser next;
    
    public void dispense(ATM atm, int amount) {
        int notes = Math.min(amount / 2000, atm.getTwoThousandCount());
        atm.setTwoThousandCount(atm.getTwoThousandCount() - notes);
        int remainder = amount - notes * 2000;
        
        if (remainder > 0 && next != null) {
            next.dispense(atm, remainder);  // Pass to next
        }
    }
}
```

**Benefits**: ✅ Single responsibility ✅ Easy to add ₹50, ₹10 notes ✅ Testable

### 3. **Factory Pattern**
**Purpose**: Create state objects

```java
public class ATMStateFactory {
    public static ATMState getState(ATMStatus status, ATMMachine machine) {
        return switch (status) {
            case IDLE -> new IdleState(machine);
            case CARD_INSERTED -> new CardInsertedState(machine);
            // ... other states
        };
    }
}
```

### 4. **Repository Pattern**
**Purpose**: Abstract ATM storage

```java
public class ATMRepository {
    private Map<String, ATM> atms = new HashMap<>();
    public void save(ATM atm) { atms.put(atm.getId(), atm); }
    public Optional<ATM> getById(String id) { return Optional.ofNullable(atms.get(id)); }
}
```

---

## Class Diagram

See [ATM_UML.drawio](diagrams/ATM_UML.drawio)

**Key Relationships**:
- ATMMachine ◆→ ATM (Composition)
- ATMMachine ○→ ATMState (Delegates to current state)
- ATMState ←── 4 State implementations
- CashDispenser ←── 3 Dispenser implementations (Chain)
- Card ◆→ Account

---

## Code Walkthrough

### 1. Core Models
```java
// ATM - Holds cash inventory and state
public class ATM {
    private final String id;
    private ATMStatus status;
    private int twoThousandCount, fiveHundredCount, oneHundredCount;
    // Constructor calculates total cash available
}

// Card - Links to account
public class Card {
    private final String cardNumber;
    private final String pin;
    private final Account account;
}

// Account - Holds balance
public class Account {
    private final String accountNumber;
    private double balance;
}
```

### 2. State Pattern Implementation
```java
// Interface defining all operations
public interface ATMState {
    void insertCard(Card card);
    void enterPin(String pin);
    void selectOption(String option);
    void dispenseCash(int amount);
    void ejectCard();
}

// IdleState - Waiting for card
public class IdleState implements ATMState {
    public void insertCard(Card card) {
        atmMachine.setCurrentCard(card);
        atmMachine.setState(new CardInsertedState(atmMachine));
    }
    // Other methods: Print "No card inserted"
}

// CardInsertedState - Validate PIN
public class CardInsertedState implements ATMState {
    public void enterPin(String pin) {
    
**Solution**: Each state is a separate class with its own behavior

**Trade-off**: 
- ✅ Clean, maintainable code
- ✅ Easy to add new states
- ❌ More classes to manage

### 2. **Why Chain of Responsibility for Cash Dispensing?**
**Problem**: Complex logic to determine optimal denomination combination

**Solution**: Each dispenser handles one denomination, chain handles the rest

**Trade-off**:
- ✅ Single Responsibility Principle
- ✅ Easy to add/remove denominations
- ✅ Testable individually
- ❌ Greedy algorithm (not always optimal)

**Alternative Considered**: Dynamic Programming for optimal combination
- More complex
- Overkill for this use case
- Greedy works fine in practice

### 3. **Why Repository Pattern?**
**Problem**: Business logic coupled with data access

**Solution**: Abstract data persistence behind repository interface

**Trade-off**:
- ✅ Testable with mock repository
- ✅ Easy to swap storage (in-memory → DB)
- ❌ Additional abstraction layer

### 4. **State Transition Management**
**Approach**: States create next state directly

**Alternative**: State machine with transition table
- More formal
- Better for complex state graphs
- Current approach is simpler for linear flows

### 5. **Immutability**
**Entities**: ATM id, Card fields are immutable

**Benefits**:
- Thread-safe
- Prevents accidental modifications
- Clearer intent

### 6. **Error Handling**
**Current**: Print errors and return to idle

**Production Alternative**:
- Return Result<T, Error> objects
- Throw custom exceptions
- Log errors for audit
- Retry mechanism for transient failures

---

## Extensibility

### 1. Adding New States
To add a new operation like "Check Balance":

```java
// Step 1: Create new state
public class CheckBalanceState implements ATMState {
    public void checkBalance() {
        double balance = atmMachine.getCurrentCard().getAccount().getBalance();
        System.out.println("Balance: " + balance);
        atmMachine.setState(new AuthenticatedState(atmMachine));
    }
}

// Step 2: Update AuthenticatedState to route to new state
public void selectOption(String option) {
    if (option.equals("WITHDRAW")) {
        atmMachine.setState(new DispenseCashState(atmMachine));
    } else if (option.equals("CHECK_BALANCE")) {
        atmMachine.setState(new CheckBalanceState(atmMachine));
    }
}
```

### 2. Adding New Denominations
To add ₹50 notes:

```java
// Step 1: Add field to ATM model
private int fiftyCount;

// Step 2: Create dispenser
public class FiftyDispenser implements CashDispenser {
    public void dispense(ATM atm, int amount) {
        int notes = Math.min(amount / 50, atm.getFiftyCount());
        atm.setFiftyCount(atm.getFiftyCount() - notes);
        // Pass remainder to next dispenser
    }
}

// Step 3: Update chain builder
public static CashDispenser buildChain() {
    // ₹2000 → ₹500 → ₹100 → ₹50
    d3.setNextDispenser(new FiftyDispenser());
}
```

### 3. Supporting Different Card Types
```java
public abstract class Card {
    public abstract boolean canWithdraw(int amount);
}Why State Pattern?
**Problem**: Complex if-else for state management  
**Solution**: Each state = separate class  
**Trade-off**: ✅ Clean code, ❌ More classes

### 2. Why Chain of Responsibility?
**Problem**: Complex denomination logic  
**Solution**: Each dispenser handles one denomination  
**Trade-off**: ✅ SRP, ✅ Extensible, ❌ Greedy (not optimal)  
**Alternative**: Dynamic Programming (overkill)

### 3. Why States Create Next State?
**Approach**: Direct state creation  
**Alternative**: State machine with transition table (better for complex graphs)

### 4. Thread Safety
**Current**: Not thread-safe  
**Solution**: Add `synchronized` on critical methods or use lock
2. **State Objects**: O(1)
   - Small constant number of states per ATM

3. **Cash Dispenser Chain**: O(d)
   - d = number of denominations

4. **Transaction Processing**: O(1)
   - No additional data structures during transaction

### Scalability Considerations

1. **Multiple ATMs**: 
   - Repository pattern supports horizontal scaling
   - Each ATM operates independently

2. **Concurrent Users**:
   - Add locking mechanism for thread safety
   - Consider optimistic locking for database updates

| Operation | Time | Space |
|-----------|------|-------|
| Card Insert | O(1) | O(1) |
| PIN Validate | O(1) | O(1) |
| Cash Dispense | O(d) | O(1) |
| ATM Lookup | O(1) | O(1) |
| State Transition | O(1) | O(1) |

*d = number of denominations (typically 3)*
```java
@Test
void testStateTransition() {
    IdleState state = new IdleState(machine);
    state.insertCard(card);
    verify(machine).setState(any(CardInsertedState.class));
}

@Test
void testCashDispenser() {
    ATM atm = new ATM("ATM1", 5, 5, 20);
    CashDispenser chain = buildChain();
    assertTrue(chain.canDispense(atm, 3100)); // 1x2000 + 2x500 + 1x100
    assertFalse(chain.canDispense(atm, 250)); // Can't dispense
}
```

### Integration Test
```java
@Test
void testCompleteFlow() {
    machine.insertCard(card);
    machine.enterPin("1234");
    machine.selectOption("WITHDRAW");
    machine.dispenseCash(1400);
    
    assertEquals(3600, card.getAccount().getBalance());
}
```

### Edge Cases
- Invalid PIN, Insufficient cash/balance, Amount not dispensable, Concurrent access
        // Update ATM inventory
        // Credit account
        // Print receipt
    }
}
```

### Q6: How would you handle network failures during transactions?
**Answer**: Implement idempotency and transaction logging
```java
public class TransactionLog {
    private String transactionId;
    private TransactionStatus status;
    
    public void markComplete() {
        this.status = TransactionStatus.COMPLETED;
        persist();
    }
    
    public void markFailed() {
        this.status = TransactionStatus.FAILED;
        persist();
    }
}
```

### Q7: How would you implement transaction limits?
**Answer**: Add validation layer
```java
public class WithdrawalLimitValidator {
    private static final int DAILY_LIMIT = 50000;
    private static final int TRANSACTION_LIMIT = 10000;
    
    public boolean validate(Card card, int amount) {
        int to handle concurrent access?
**Answer**: Use `ReentrantLock` or `synchronized` on critical methods

### Q2: How to optimize denomination dispensing?
**Answer**: Dynamic Programming for optimal combination (minimize notes)

### Q3: How to handle transaction rollback?
**Answer**: Snapshot state before transaction, restore on failure

### Q4: How to add deposit functionality?
**Answer**: Create `DepositState`, reverse cash flow (add to ATM, credit account)

### Q5: How to implement transaction limits?
**Answer**: Add `WithdrawalLimitValidator` (daily: ₹50k, per-transaction: ₹10k)

### Q6: How to support different card types?
**Answer**: Abstract `Card` class with `canWithdraw()`, `DebitCard`/`CreditCard` implementations

### Q7: How to add audit logging?
**Answer**: `AuditLogger` service, log all state transitions and operations

### Q8: How to scale for millions of ATMs?
**Answer**: Database sharding (by geography), caching (Redis), event sourcing, microservices