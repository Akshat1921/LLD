package com.example.LLD.Splitwise.split;

import com.example.LLD.Splitwise.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Split {
    private double amountOwe;
    private User user;
}
