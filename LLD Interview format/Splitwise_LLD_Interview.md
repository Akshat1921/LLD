# Splitwise - Low Level Design Interview

## Problem Statement

Design **Splitwise** - an expense sharing application that allows users to split bills, track balances, and settle debts.

**Core Features**:
- Add users and create groups
- Create expenses (Equal/Unequal/Percentage split)
- Track who owes whom
- View balance sheets

---

## Requirements

### Functional Requirements
1. **User Management**: Add/retrieve users
2. **Group Management**: Create groups, add members
3. **Expense Creation**: Split expenses equally, unequally, or by percentage
4. **Balance Tracking**: Track per-user balances (owe & get back)
5. **Balance Sheet**: View simplified balances

### Non-Functional Requirements
1. **Extensibility**: Easy to add new split strategies
2. **Validation**: Ensure split amounts match total expense
3. **Simplification**: Minimize number of transactions

---

## Core Entities

| Entity | Description | Key Attributes |
|--------|-------------|----------------|
| **User** | Person using Splitwise | userId, userName, balanceSheet |
| **Group** | Collection of users sharing expenses | groupId, members, expenses |
| **Expense** | Bill to be split | expenseId, amount, paidBy, splits |
| **Split** | Individual's share of expense | user, amountOwe |
| **Balance** | Balance between two users | amountOwe, amountGetBack |
| **UserExpenseBalanceSheet** | User's overall balance | userVsBalance, totals |

---

## Design Patterns

### 1. **Strategy Pattern** ⭐
**Purpose**: Different algorithms for splitting expenses

**Strategies**:
- `EqualExpenseSplit`: Divide equally among all
- `UnequalExpenseSplit`: Custom amounts per person
- `PercentageExpenseSplit`: Split by percentages

**Implementation**:
```java
public interface ExpenseSplitStrategy {
    void validateSplitRequest(List<Split> splits, double totalAmount);
}

public class EqualExpenseSplit implements ExpenseSplitStrategy {
    public void validateSplitRequest(List<Split> splits, double totalAmount) {
        double equalAmount = totalAmount / splits.size();
        // Validate each split equals equalAmount
    }
}
```

**Selection in ExpenseController**:
```java
ExpenseSplitStrategy strategy = switch(splitType) {
    case EQUAL -> new EqualExpenseSplit();
    case UNEQUAL -> new UnequalExpenseSplit();
    case PERCENTAGE -> new PercentageExpenseSplit();
};
strategy.validateSplitRequest(splits, amount);
```

**Benefits**:
- ✅ Easy to add new split types (ExactAmount, Ratio)
- ✅ Each strategy encapsulates validation logic
- ✅ Open/Closed Principle

### 2. **Singleton Pattern**
**Purpose**: Single Splitwise instance

```java
public class Splitwise {
    private static Splitwise instance;
    
    private Splitwise() { }  // Private constructor
    
    public static Splitwise getInstance() {
        if (instance == null) {
            synchronized(Splitwise.class) {
                if (instance == null) {
                    instance = new Splitwise();
                }
            }
        }
        return instance;
    }
}
```

**Benefits**: Centralized control, shared state

### 3. **MVC Pattern**
**Structure**:
- **Models**: User, Group, Expense, Split
- **Controllers**: UserController, GroupController, ExpenseController, BalanceSheetController
- **View**: Console output

---

## Code Walkthrough

### 1. Models
```java
// User with balance sheet
public class User {
    private String userId;
    private String userName;
    private UserExpenseBalanceSheet balanceSheet;
}

// Group managing expenses
public class Group {
    private String groupId;
    private List<User> members;
    private List<Expense> expenses;
    private ExpenseController expenseController;
    
    public void addMember(User user) { members.add(user); }
    
    public Expense createExpense(String id, String desc, double amount,
                                 List<Split> splits, ExpenseSplitType type, User paidBy) {
        Expense expense = expenseController.createExpense(...);
        expenses.add(expense);
        return expense;
    }
}

// Expense with splits
public class Expense {
    public String expenseId;
    public double expenseAmount;
    public User paidByUser;
    public ExpenseSplitType splitType;
    public List<Split> splitDetails;
}

// Individual split
public class Split {
    private double amountOwe;
    private User user;
}
```

### 2. Controllers
```java
// ExpenseController - Creates and validates expenses
public class ExpenseController {
    private BalanceSheetController balanceController;
    
    public Expense createExpense(...) {
        // 1. Select strategy based on split type
        ExpenseSplitStrategy strategy = getStrategy(splitType);
        
        // 2. Validate splits
        strategy.validateSplitRequest(splits, amount);
        
        // 3. Create expense
        Expense expense = new Expense(...);
        
        // 4. Update balance sheets
        balanceController.updateUserExpenseBalanceSheet(paidBy, splits, amount);
        
        return expense;
    }
}

// BalanceSheetController - Updates balances
public class BalanceSheetController {
    public void updateUserExpenseBalanceSheet(User paidBy, List<Split> splits, double amount) {
        // Update paidBy's balance (they should get money back)
        // Update each split user's balance (they owe money)
    }
}
```

### 3. Usage Example
```java
Splitwise app = Splitwise.getInstance();

// Create users
User alice = new User("U1", "Alice");
User bob = new User("U2", "Bob");
User charlie = new User("U3", "Charlie");

// Create group
Group group = new Group();
group.addMember(bob);
group.addMember(charlie);

// Equal split: Dinner for ₹900
group.createExpense(
    "E1", "Dinner", 900,
    Arrays.asList(
        new Split(300, alice),
        new Split(300, bob),
        new Split(300, charlie)
    ),
    ExpenseSplitType.EQUAL,
    alice  // Alice paid
);

// Result: Bob owes Alice ₹300, Charlie owes Alice ₹300
```

---

## Class Diagram

See [Splitwise_UML.drawio](diagrams/Splitwise_UML.drawio)

**Key Relationships**:
- User ◆→ UserExpenseBalanceSheet (Composition)
- Group ◆→ Expense (Composition 1..*)
- Expense → Split (Association 1..*)
- Split → User (Association)
- ExpenseSplitStrategy ←── 3 Implementations (Strategy Pattern)

---

## Design Decisions

### 1. Why Strategy Pattern?
**Problem**: Different split algorithms  
**Solution**: Strategy interface with multiple implementations  
**Trade-off**: ✅ Extensible, ❌ More classes

### 2. Why Store Balance in User?
**Problem**: Need quick access to user balances  
**Solution**: Each user has their own balance sheet  
**Alternative**: Central balance manager (more complex)

### 3. Why Group Contains ExpenseController?
**Problem**: Groups need to create expenses  
**Solution**: Each group has its own controller  
**Trade-off**: ✅ Encapsulation, ❌ Memory overhead

### 4. Validation Strategy Selection
**Approach**: Switch statement in controller  
**Alternative**: Factory pattern (overkill for 3 types)

---

## Extensibility

### Adding New Split Type
```java
// 1. Add enum value
public enum ExpenseSplitType {
    EQUAL, UNEQUAL, PERCENTAGE, EXACT  // New
}

// 2. Create strategy
public class ExactExpenseSplit implements ExpenseSplitStrategy {
    public void validateSplitRequest(List<Split> splits, double totalAmount) {
        double sum = splits.stream().mapToDouble(Split::getAmountOwe).sum();
        if (sum != totalAmount) {
            throw new IllegalArgumentException("Sum doesn't match total");
        }
    }
}

// 3. Update controller
case EXACT -> new ExactExpenseSplit();
```

### Adding Notifications
```java
public interface NotificationService {
    void notifyExpense(User user, Expense expense);
}

// In ExpenseController after creating expense
notificationService.notifyExpense(paidBy, expense);
```

### Simplifying Balances
```java
public class BalanceSimplifier {
    public List<Transaction> simplify(List<User> users) {
        // Use graph algorithms to minimize transactions
        // Return list of: "A pays B: ₹X"
    }
}
```

---

## Complexity Analysis

| Operation | Time | Space |
|-----------|------|-------|
| Add User | O(1) | O(1) |
| Create Expense | O(n) | O(n) |
| Update Balance | O(n) | O(1) |
| Get User Balance | O(1) | O(1) |

*n = number of splits in expense*

---

## Testing Strategy

### Unit Tests
```java
@Test
void testEqualSplit_validSplits_passes() {
    List<Split> splits = Arrays.asList(
        new Split(300, alice),
        new Split(300, bob)
    );
    
    strategy.validateSplitRequest(splits, 600);
    // Should pass
}

@Test
void testEqualSplit_invalidSplits_throws() {
    List<Split> splits = Arrays.asList(
        new Split(400, alice),
        new Split(200, bob)
    );
    
    assertThrows(IllegalArgumentException.class, 
        () -> strategy.validateSplitRequest(splits, 600));
}
```

### Integration Test
```java
@Test
void testCompleteExpenseFlow() {
    Group group = new Group();
    group.addMember(bob);
    
    Expense expense = group.createExpense(...);
    
    // Verify balances updated
    assertEquals(600, alice.getBalanceSheet().getTotalYouGetBack());
    assertEquals(300, bob.getBalanceSheet().getTotalYouOwe());
}
```

---

## Follow-up Questions

### Q1: How to handle partial payments?
**Answer**: Add `amountPaid` field to Expense, track settlements separately

### Q2: How to minimize number of transactions?
**Answer**: Graph algorithm - Calculate net balance, match debtors with creditors

### Q3: How to add currency support?
**Answer**: Add `Currency` enum, `CurrencyConverter` service

### Q4: How to handle refunds?
**Answer**: Create negative expense or dedicated `Refund` entity

### Q5: How to support recurring expenses?
**Answer**: Add `RecurringExpense` with schedule, auto-create monthly

### Q6: How to add expense categories?
**Answer**: Add `ExpenseCategory` enum (Food, Travel, Rent)

### Q7: How to handle deleted users?
**Answer**: Soft delete, archive balances, prevent new expenses

### Q8: How to scale for millions of users?
**Answer**: 
- Shard by userId
- Cache user balances in Redis
- Event sourcing for expense history
- Microservices (User, Group, Expense services)

---

## Summary

**Design Patterns Used**:
- ✅ **Strategy Pattern**: Flexible expense splitting
- ✅ **Singleton Pattern**: Single app instance
- ✅ **MVC Pattern**: Separation of concerns

**Key Takeaways**:
1. Strategy pattern makes split logic extensible
2. Balance tracking in user simplifies queries
3. Validation ensures data consistency
4. Controllers manage business logic

---

**UML Diagram**: [diagrams/Splitwise_UML.drawio](diagrams/Splitwise_UML.drawio)  
**Source Code**: `src/main/java/com/example/LLD/Splitwise/`

**Last Updated**: March 31, 2026  
**Version**: 1.0
