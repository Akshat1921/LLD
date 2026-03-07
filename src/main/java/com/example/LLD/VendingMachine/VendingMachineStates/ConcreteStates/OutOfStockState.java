package com.example.LLD.VendingMachine.VendingMachineStates.ConcreteStates;

import com.example.LLD.VendingMachine.VendingMachineStates.VendingMachineContext;
import com.example.LLD.VendingMachine.VendingMachineStates.VendingMachineState;

public class OutOfStockState implements VendingMachineState{
    public OutOfStockState() {
        System.out.println("Vending machine is now in Out of Stock State");
    }

    @Override
    public String getStateName() {
        return "OutOfStockState";
    }

    @Override
    public VendingMachineState next(VendingMachineContext context){
        if(context.getInventory().hasItems()){
            return new IdleState();
        }
        return this;
    }
}
