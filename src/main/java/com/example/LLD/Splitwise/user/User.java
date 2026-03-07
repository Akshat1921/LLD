package com.example.LLD.Splitwise.user;

import com.example.LLD.Splitwise.balance.UserExpenseBalanceSheet;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String userId;
    private String userName;
    private UserExpenseBalanceSheet userExpenseBalanceSheet;

    public User(String userId, String userName){
        this.userId = userId;
        this.userName = userName;
        this.userExpenseBalanceSheet = new UserExpenseBalanceSheet();
    }

}
