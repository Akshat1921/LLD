package com.example.LLD.Splitwise.split.SplitStrategies;

import java.util.List;

import com.example.LLD.Splitwise.split.ExpenseSplitStrategy;
import com.example.LLD.Splitwise.split.Split;

public class UnequalExpenseSplit implements ExpenseSplitStrategy{
    @Override
    public void validateSplitRequest(List<Split> splits, double totalAmount){
        double sum = splits.stream().mapToDouble(e->e.getAmountOwe()).sum();
        if(sum!=totalAmount){
            throw new IllegalArgumentException("Split amounts do not match the total amount");
        }
    }
}
