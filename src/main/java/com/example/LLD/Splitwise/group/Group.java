package com.example.LLD.Splitwise.group;

import java.util.ArrayList;
import java.util.List;

import com.example.LLD.Splitwise.controllers.ExpenseController;
import com.example.LLD.Splitwise.expense.Expense;
import com.example.LLD.Splitwise.expense.ExpenseSplitType;
import com.example.LLD.Splitwise.split.Split;
import com.example.LLD.Splitwise.user.User;

import lombok.Data;

@Data
public class Group {
    private String groupId;
    private String groupName;
    private List<User> groupMembers = new ArrayList<>();
    private List<Expense> expenseList = new ArrayList<>();
    private ExpenseController expenseController = new ExpenseController();

    public void addMember(User member){
        if (groupMembers.contains(member)) {
            System.out.println("User " + member.getUserName() + " is already a member of the group!");
        } else {
            groupMembers.add(member);
            System.out.println("User " + member.getUserName() + " added to the group.");
        }
    }

    public Expense createExpense(String expenseId, String description, double expenseAmount,
                                 List<Split> splitDetails, ExpenseSplitType splitType, User paidByUser){
        Expense expense = expenseController.createExpense(expenseId, description, expenseAmount, splitDetails, splitType, paidByUser);
        expenseList.add(expense);
        return expense;
    }

}
