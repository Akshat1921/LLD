package com.example.LLD.VendingMachine.Enums;

public enum Coin {
    ONE_RUPEE(1),
    TWO_RUPEE(2),
    FIVE_RUPEE(5),
    TEN_RUPEE(10);

    public int value;

    Coin(int value){
        this.value = value;
    }

}
