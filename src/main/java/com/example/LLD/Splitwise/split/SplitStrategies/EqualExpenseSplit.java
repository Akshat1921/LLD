package com.example.LLD.Splitwise.split.SplitStrategies;

import java.util.List;

import com.example.LLD.Splitwise.split.ExpenseSplitStrategy;
import com.example.LLD.Splitwise.split.Split;

public class EqualExpenseSplit implements ExpenseSplitStrategy{
    @Override
    public void validateSplitRequest(List<Split> splits, double totalAmount){
        double amountShouldBePresent = totalAmount/splits.size();
        splits.stream().filter(e->e.getAmountOwe()!=amountShouldBePresent).findAny().ifPresent(split -> {
            throw new IllegalArgumentException("Each person should have an equal split");
        });
    }
}
