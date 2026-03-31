package com.example.LLD.ATM.states;

import com.example.LLD.ATM.cors.CashDispenser;
import com.example.LLD.ATM.cors.CashDispenserChainBuilder;
import com.example.LLD.ATM.enums.ATMStatus;
import com.example.LLD.ATM.models.Card;
import com.example.LLD.ATM.service.ATMMachine;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DispenseCashState implements ATMState {
    private final ATMMachine atmMachine;
    private final CashDispenser chain = CashDispenserChainBuilder.buildChain();

    @Override
    public void insertCard(Card card) {
        System.out.println("Transaction in progress. Cannot insert another card.");
    }

    @Override
    public void enterPin(String pin) {
        System.out.println("Already authenticated.");
    }

    @Override
    public void selectOption(String option) {
        System.out.println("Option already selected.");
    }

    @Override
    public void dispenseCash(int amount) {
        double atmBalance = atmMachine.getAtm().getCashAvailable();
        double accountBalance = atmMachine.getCurrentCard()
                .getAccount()
                .getBalance();


        if (amount > atmBalance) {
            System.out.println("ATM has insufficient cash. Cannot dispense " + amount);
            ejectCard();
            return;
        }

        if (amount > accountBalance) {
            System.out.println("Insufficient account balance.");
            ejectCard();
            return;
        }

        // Now check if note combination is possible
        if (chain.canDispense(atmMachine.getAtm(), amount)) {
            chain.dispense(atmMachine.getAtm(), amount);

            // Deduct from ATM cash & account balance
            atmMachine.getAtm().setCashAvailable(atmBalance - amount);
            atmMachine.getCurrentCard().getAccount().setBalance(accountBalance - amount);

            ejectCard();
            System.out.println("Cash dispensed: " + amount);
        } else {
            System.out.println("Cannot dispense requested amount with available denominations.");
            ejectCard();
        }
    }

    @Override
    public void ejectCard() {
        atmMachine.setCurrentCard(null);
        System.out.println("Card ejected.");
        atmMachine.setState(new IdleState(atmMachine)); // use factory
    }

    @Override
    public ATMStatus getStatus() {
        return ATMStatus.DISPENSE_CASH;
    }
}