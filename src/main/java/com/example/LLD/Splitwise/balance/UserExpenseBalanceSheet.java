package com.example.LLD.Splitwise.balance;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class UserExpenseBalanceSheet {
    private Map<String, Balance> userVsBalance;
    private double totalPayment;
    private double totalYourExpense;
    private double totalYouOwe;
    private double totalYouGetBack;

    public UserExpenseBalanceSheet(){
        totalYouGetBack = 0;
        totalYouOwe = 0;
        totalYourExpense = 0;
        userVsBalance = new HashMap<>();
    }

}
