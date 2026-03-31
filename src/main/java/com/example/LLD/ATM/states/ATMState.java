package com.example.LLD.ATM.states;

import com.example.LLD.ATM.enums.ATMStatus;
import com.example.LLD.ATM.models.Card;

public interface ATMState {
    void insertCard(Card card);
    void enterPin(String pin);
    void selectOption(String option);
    void dispenseCash(int amount);
    void ejectCard();
    ATMStatus getStatus();
}
