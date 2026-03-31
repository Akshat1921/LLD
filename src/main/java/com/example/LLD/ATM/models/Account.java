package com.example.LLD.ATM.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Account {
    private final String accountNumber;
    @Setter 
    private double balance;
}
