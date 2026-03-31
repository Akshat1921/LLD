package com.example.LLD.ATM.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Card {
    private final String cardNumber;
    private final String pin;
    private final Account account;
}