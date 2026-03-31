package com.example.LLD.ATM.factory;

import com.example.LLD.ATM.enums.ATMStatus;
import com.example.LLD.ATM.service.ATMMachine;
import com.example.LLD.ATM.states.ATMState;
import com.example.LLD.ATM.states.AuthenticatedState;
import com.example.LLD.ATM.states.CardInsertedState;
import com.example.LLD.ATM.states.DispenseCashState;
import com.example.LLD.ATM.states.IdleState;

public class ATMStateFactory {

    public static ATMState getState(ATMStatus status, ATMMachine machine) {
        return switch (status) {
            case IDLE -> new IdleState(machine);
            case CARD_INSERTED -> new CardInsertedState(machine);
            case AUTHENTICATED -> new AuthenticatedState(machine);
            case DISPENSE_CASH -> new DispenseCashState(machine);
            default -> throw new IllegalArgumentException("Unknown ATM status: " + status);
        };
    }
}