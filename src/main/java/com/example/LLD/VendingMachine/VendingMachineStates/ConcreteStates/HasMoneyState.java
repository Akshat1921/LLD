package com.example.LLD.VendingMachine.VendingMachineStates.ConcreteStates;

import com.example.LLD.VendingMachine.VendingMachineStates.VendingMachineContext;
import com.example.LLD.VendingMachine.VendingMachineStates.VendingMachineState;

public class HasMoneyState implements VendingMachineState{
    public HasMoneyState() {
        System.out.println("Vending machine is now in HasMoney State");
    }

    @Override
    public String getStateName() {
        return "HasMoneyState";
    }

    @Override
    public VendingMachineState next(VendingMachineContext context){
        if(!context.getInventory().hasItems()){
            return new OutOfStockState();
        }
        if(!context.getCoinList().isEmpty()){
            return new IdleState();
        }
        if(context.getCurrentState() instanceof HasMoneyState){
            return new SelectionState();
        }
        return this;
    }

}
