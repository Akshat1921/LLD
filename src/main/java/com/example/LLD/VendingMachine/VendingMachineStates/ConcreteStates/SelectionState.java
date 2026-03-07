package com.example.LLD.VendingMachine.VendingMachineStates.ConcreteStates;

import com.example.LLD.VendingMachine.VendingMachineStates.VendingMachineContext;
import com.example.LLD.VendingMachine.VendingMachineStates.VendingMachineState;

public class SelectionState implements VendingMachineState{
    public SelectionState(){
        System.out.println("Vending machine is now in idle state");
    }
    @Override
    public String getStateName(){
        return "SelectionState";
    }

    @Override
    public VendingMachineState next(VendingMachineContext context){
        
        if(!context.getInventory().hasItems()){
            return new OutOfStockState();
        }
        
        if(!context.getCoinList().isEmpty()){
            return new IdleState();
        }
        
        if(context.getSelectedItemCode()>0){
            return new DispenseState();
        }
        return this;
    }
}
