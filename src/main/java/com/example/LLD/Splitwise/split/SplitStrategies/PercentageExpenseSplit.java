package com.example.LLD.Splitwise.split.SplitStrategies;

import java.util.List;

import com.example.LLD.Splitwise.split.ExpenseSplitStrategy;
import com.example.LLD.Splitwise.split.Split;

public class PercentageExpenseSplit implements ExpenseSplitStrategy{
    @Override
    public void validateSplitRequest(List<Split> splits, double totalAmount){
        double totalPercent = splits.stream().mapToDouble(e->e.getAmountOwe()).sum();
        if (totalPercent != totalAmount) {
            throw new IllegalArgumentException("Total percentage must sum up to 100%");
        }
    }
}
