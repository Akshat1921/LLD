package com.example.LLD.Splitwise.split;

import java.util.List;

public interface ExpenseSplitStrategy {
    public void validateSplitRequest(List<Split> splits, double totalAmount);
}
