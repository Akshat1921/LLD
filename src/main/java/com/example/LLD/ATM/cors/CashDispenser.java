package com.example.LLD.ATM.cors;

import com.example.LLD.ATM.models.ATM;

public interface CashDispenser {
    void setNextDispenser(CashDispenser next);
    boolean canDispense(ATM atm, int amount);
    void dispense(ATM atm, int amount);
}
