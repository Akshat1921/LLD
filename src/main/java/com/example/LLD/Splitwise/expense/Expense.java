package com.example.LLD.Splitwise.expense;

import java.util.ArrayList;
import java.util.List;

import com.example.LLD.Splitwise.split.Split;
import com.example.LLD.Splitwise.user.User;

public class Expense {
    public String expenseId;
    public String description;
    public List<Split> splitDetails = new ArrayList<>();
    public double expenseAmount;
    public User paidByUser;
    public ExpenseSplitType splitType;

    public Expense(String expenseId, double expenseAmount, String description, User paidByUser, ExpenseSplitType splitType, List<Split> splitDetails){
        this.expenseId = expenseId;
        this.expenseAmount = expenseAmount;
        this.description = description;
        this.paidByUser = paidByUser;
        this.splitType = splitType;
        this.splitDetails.addAll(splitDetails);
    }

}
