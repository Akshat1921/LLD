package com.example.LLD.Splitwise.controllers;

import java.util.List;
import java.util.Map;

import com.example.LLD.Splitwise.balance.Balance;
import com.example.LLD.Splitwise.balance.UserExpenseBalanceSheet;
import com.example.LLD.Splitwise.split.Split;
import com.example.LLD.Splitwise.user.User;

//✅ Simple logic in steps:
//
//Step 1: Update total expense paid.
//Step 2: Loop through each person who needs to pay.
//Step 3: Update payer's balance (kitna wapas aana chahiye).
//Step 4: Update owe karne wale ka balance (kitna paisa dena hai).

public class BalanceSheetController {
    public void updateUserExpenseBalanceSheet(User payer, List<Split> splits, double totalExpense){
        UserExpenseBalanceSheet payerSheet = payer.getUserExpenseBalanceSheet();
        payerSheet.setTotalPayment(payerSheet.getTotalPayment() + totalExpense);

        for(Split split: splits){
            User personWhoOwes = split.getUser();
            UserExpenseBalanceSheet oweSheet = personWhoOwes.getUserExpenseBalanceSheet();
            double amountToPay = split.getAmountOwe();

            if(payer.getUserId().equals(personWhoOwes.getUserId())){
                payerSheet.setTotalYourExpense(payerSheet.getTotalYourExpense()+amountToPay);
            }else{
                payerSheet.setTotalYouGetBack(payerSheet.getTotalYouGetBack()+amountToPay);

                Balance payerBalance = payerSheet.getUserVsBalance().computeIfAbsent(personWhoOwes.getUserId(), k-> new Balance());
                payerBalance.setAmountGetBack(payerSheet.getTotalYouGetBack()+amountToPay);

                oweSheet.setTotalYouOwe(oweSheet.getTotalYouOwe()+amountToPay);
                oweSheet.setTotalYourExpense(oweSheet.getTotalYourExpense()+amountToPay);
                
                Balance oweBalance = oweSheet.getUserVsBalance().computeIfAbsent(payer.getUserId(), k-> new Balance());
                oweBalance.setAmountOwe(oweBalance.getAmountOwe()+amountToPay);

            }

        }
    }
    
    public void showBalanceSheetOfUser(User user){

        System.out.println("---------------------------------------");

        System.out.println("Balance sheet of user : " + user.getUserId());

        UserExpenseBalanceSheet userExpenseBalanceSheet =  user.getUserExpenseBalanceSheet();

        System.out.println("TotalYourExpense: " + userExpenseBalanceSheet.getTotalYourExpense());
        System.out.println("TotalGetBack: " + userExpenseBalanceSheet.getTotalYouGetBack());
        System.out.println("TotalYourOwe: " + userExpenseBalanceSheet.getTotalYouOwe());
        System.out.println("TotalPaymnetMade: " + userExpenseBalanceSheet.getTotalPayment());
        for(Map.Entry<String, Balance> entry : userExpenseBalanceSheet.getUserVsBalance().entrySet()){

            String userID = entry.getKey();
            Balance balance = entry.getValue();

            System.out.println("userID:" + userID + " YouGetBack:" + balance.getAmountGetBack() + " YouOwe:" + balance.getAmountOwe());
        }

        System.out.println("---------------------------------------");

    }
}
