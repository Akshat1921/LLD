package com.example.LLD.ATM;

import com.example.LLD.ATM.models.ATM;
import com.example.LLD.ATM.models.Account;
import com.example.LLD.ATM.models.Card;
import com.example.LLD.ATM.repo.ATMRepository;
import com.example.LLD.ATM.service.ATMMachine;

public class Main {
    public static void main(String[] args) {
        Card card = new Card(
                "CARD123",
                "1234",
                new Account("ACC123", 5000)
        );

        ATM atm1 = new ATM("ATM1", 5, 5, 20);
        ATM atm2 = new ATM("ATM2", 0, 2, 5);

        ATMRepository atmRepository = new ATMRepository();
        atmRepository.save(atm1);
        atmRepository.save(atm2);

        ATMMachine atmMachine2 = new ATMMachine("ATM2", atmRepository);

        atmMachine2.insertCard(card);
        atmMachine2.enterPin("1234");
        atmMachine2.selectOption("WITHDRAW");
        atmMachine2.dispenseCash(1410);
    }
}