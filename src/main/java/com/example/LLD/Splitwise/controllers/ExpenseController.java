package com.example.LLD.Splitwise.controllers;

import java.util.List;

import com.example.LLD.Splitwise.expense.Expense;
import com.example.LLD.Splitwise.expense.ExpenseSplitType;
import com.example.LLD.Splitwise.split.ExpenseSplitStrategy;
import com.example.LLD.Splitwise.split.Split;
import com.example.LLD.Splitwise.split.SplitStrategies.EqualExpenseSplit;
import com.example.LLD.Splitwise.split.SplitStrategies.PercentageExpenseSplit;
import com.example.LLD.Splitwise.split.SplitStrategies.UnequalExpenseSplit;
import com.example.LLD.Splitwise.user.User;

public class ExpenseController {
    private BalanceSheetController balanceSheetController;

    public ExpenseController(){
        balanceSheetController = new BalanceSheetController();
    }

    public Expense createExpense(String expenseId, String description, double expenseAmount, List<Split> splitDetails, ExpenseSplitType splitType, User paidByUser){
        
        ExpenseSplitStrategy expenseSplit;
        switch (splitType) {
            case EQUAL:
                expenseSplit = new EqualExpenseSplit();
                break;
            case UNEQUAL:
                expenseSplit = new UnequalExpenseSplit();
                break;
            case PERCENTAGE:
                expenseSplit = new PercentageExpenseSplit();
                break;
            default:
                throw new IllegalArgumentException("Invalid split type");
        }

        expenseSplit.validateSplitRequest(splitDetails, expenseAmount);

        Expense expense = new Expense(expenseId, expenseAmount, description, paidByUser, splitType, splitDetails);

        
        balanceSheetController.updateUserExpenseBalanceSheet(paidByUser, splitDetails, expenseAmount);
        return expense;
    }

}
